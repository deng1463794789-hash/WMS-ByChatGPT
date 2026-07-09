package com.wms.modules.market.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.common.exception.BusinessException;
import com.wms.modules.market.dto.MarketQuoteResponse;
import com.wms.modules.market.dto.MarketTrendPoint;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class MarketQuoteService {

    private static final String DEFAULT_SYMBOLS = "sh600362,sz000630,sz000878,sh601899,sh603993,sh601168,sz000737,sh601212,sz002203,sh601609";
    private static final long CACHE_TTL_MILLIS = 20000L;
    private static final int MAX_SYMBOLS = 20;
    private static final Charset TENCENT_CHARSET = Charset.forName("GBK");
    private static final DateTimeFormatter TENCENT_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;
    private volatile CacheEntry cacheEntry;

    public MarketQuoteService(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(4))
            .build();
    }

    public List<MarketQuoteResponse> getQuotes(String symbols) {
        List<String> normalizedSymbols = normalizeSymbols(symbols);
        String cacheKey = String.join(",", normalizedSymbols);
        long now = System.currentTimeMillis();
        CacheEntry cached = cacheEntry;
        if (cached != null && cached.key.equals(cacheKey) && now - cached.timestamp < CACHE_TTL_MILLIS) {
            return cached.data;
        }

        List<MarketQuoteResponse> quotes = fetchTencentQuotes(cacheKey);
        quotes.forEach((quote) -> quote.setTrendPoints(fetchEastMoneyTrend(quote.getSymbol())));
        cacheEntry = new CacheEntry(cacheKey, now, quotes);
        return quotes;
    }

    private List<String> normalizeSymbols(String symbols) {
        String raw = symbols == null || symbols.isBlank() ? DEFAULT_SYMBOLS : symbols;
        List<String> result = Arrays.stream(raw.split(","))
            .map(String::trim)
            .filter(item -> !item.isEmpty())
            .map(item -> item.toLowerCase(Locale.ROOT))
            .map(this::normalizeSymbol)
            .filter(item -> !item.isEmpty())
            .distinct()
            .limit(MAX_SYMBOLS)
            .collect(Collectors.toList());
        if (!result.isEmpty()) {
            return result;
        }
        return Arrays.asList(DEFAULT_SYMBOLS.split(","));
    }

    private String normalizeSymbol(String symbol) {
        if (symbol.matches("^(sh|sz)\\d{6}$")) {
            return symbol;
        }
        if (!symbol.matches("^\\d{6}$")) {
            return "";
        }
        char first = symbol.charAt(0);
        if (first == '6' || first == '5' || first == '9') {
            return "sh" + symbol;
        }
        if (first == '0' || first == '2' || first == '3') {
            return "sz" + symbol;
        }
        return "";
    }

    private List<MarketQuoteResponse> fetchTencentQuotes(String symbols) {
        try {
            String encodedSymbols = URLEncoder.encode(symbols, StandardCharsets.UTF_8);
            URI uri = URI.create("http://qt.gtimg.cn/q=" + encodedSymbols);
            HttpRequest request = HttpRequest.newBuilder(uri)
                .timeout(Duration.ofSeconds(6))
                .header("User-Agent", "Mozilla/5.0 WMS-Market-Proxy")
                .GET()
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(TENCENT_CHARSET));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessException("Tencent market source unavailable");
            }
            return parseTencentQuotes(response.body());
        } catch (IOException ex) {
            throw new BusinessException("Tencent market source connection failed");
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new BusinessException("Tencent market request interrupted");
        }
    }

    private List<MarketQuoteResponse> parseTencentQuotes(String body) {
        if (body == null || body.isBlank()) {
            return Collections.emptyList();
        }
        List<MarketQuoteResponse> quotes = new ArrayList<>();
        String[] rows = body.split(";\\s*");
        for (String row : rows) {
            MarketQuoteResponse quote = parseTencentRow(row);
            if (quote != null) {
                quotes.add(quote);
            }
        }
        return quotes;
    }

    private MarketQuoteResponse parseTencentRow(String row) {
        int nameStart = row.indexOf("v_");
        int equalsIndex = row.indexOf("=\"");
        int valueEnd = row.lastIndexOf('"');
        if (nameStart < 0 || equalsIndex < 0 || valueEnd <= equalsIndex) {
            return null;
        }
        String symbol = row.substring(nameStart + 2, equalsIndex).toLowerCase(Locale.ROOT);
        String payload = row.substring(equalsIndex + 2, valueEnd);
        String[] fields = payload.split("~", -1);
        if (fields.length < 35 || fields[1].isBlank() || fields[3].isBlank()) {
            return null;
        }

        MarketQuoteResponse quote = new MarketQuoteResponse();
        quote.setSymbol(symbol);
        quote.setName(fields[1]);
        quote.setCurrency("CNY");
        quote.setMarketState("Tencent CN A-share");
        quote.setPrice(decimal(fields, 3));
        quote.setPreviousClose(decimal(fields, 4));
        quote.setOpen(decimal(fields, 5));
        quote.setVolume(longValue(fields, 6));
        quote.setChange(decimal(fields, 31));
        quote.setChangePercent(decimal(fields, 32));
        quote.setDayHigh(decimal(fields, 33));
        quote.setDayLow(decimal(fields, 34));
        quote.setQuoteTime(formatQuoteTime(value(fields, 30)));
        quote.setSource("Tencent Finance + Eastmoney intraday trend");
        return quote;
    }

    private List<MarketTrendPoint> fetchEastMoneyTrend(String symbol) {
        try {
            String secId = eastMoneySecId(symbol);
            if (secId.isEmpty()) {
                return Collections.emptyList();
            }
            String url = "http://push2his.eastmoney.com/api/qt/stock/trends2/get"
                + "?secid=" + secId
                + "&fields1=f1,f2,f3,f4,f5,f6,f7,f8,f9,f10,f11,f12,f13"
                + "&fields2=f51,f52,f53,f54,f55,f56,f57,f58"
                + "&iscr=0&iscca=0&ndays=1";
            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(6))
                .header("User-Agent", "Mozilla/5.0 WMS-Market-Proxy")
                .GET()
                .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                return Collections.emptyList();
            }
            return parseEastMoneyTrend(response.body());
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }

    private String eastMoneySecId(String symbol) {
        if (symbol == null || symbol.length() != 8) {
            return "";
        }
        String code = symbol.substring(2);
        if (symbol.startsWith("sh")) {
            return "1." + code;
        }
        if (symbol.startsWith("sz")) {
            return "0." + code;
        }
        return "";
    }

    private List<MarketTrendPoint> parseEastMoneyTrend(String body) throws IOException {
        JsonNode trends = objectMapper.readTree(body).path("data").path("trends");
        if (!trends.isArray()) {
            return Collections.emptyList();
        }
        List<MarketTrendPoint> points = new ArrayList<>();
        for (JsonNode node : trends) {
            String[] fields = node.asText("").split(",", -1);
            if (fields.length < 8) {
                continue;
            }
            MarketTrendPoint point = new MarketTrendPoint();
            point.setTime(fields[0]);
            point.setPrice(parseDouble(fields[2]));
            point.setVolume(parseLong(fields[5]));
            point.setAmount(parseDouble(fields[6]));
            point.setAveragePrice(parseDouble(fields[7]));
            points.add(point);
        }
        return points;
    }

    private String formatQuoteTime(String value) {
        if (value == null || !value.matches("^\\d{14}$")) {
            return LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toString();
        }
        return LocalDateTime.parse(value, TENCENT_TIME_FORMAT)
            .atZone(ZoneId.of("Asia/Shanghai"))
            .toInstant()
            .toString();
    }

    private String value(String[] fields, int index) {
        return index >= 0 && index < fields.length ? fields[index] : "";
    }

    private Double decimal(String[] fields, int index) {
        return parseDouble(value(fields, index));
    }

    private Double parseDouble(String value) {
        if (value == null || value.isBlank()) {
            return 0D;
        }
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException ex) {
            return 0D;
        }
    }

    private Long longValue(String[] fields, int index) {
        return parseLong(value(fields, index));
    }

    private Long parseLong(String value) {
        if (value == null || value.isBlank()) {
            return 0L;
        }
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return 0L;
        }
    }

    private static class CacheEntry {
        private final String key;
        private final long timestamp;
        private final List<MarketQuoteResponse> data;

        private CacheEntry(String key, long timestamp, List<MarketQuoteResponse> data) {
            this.key = key;
            this.timestamp = timestamp;
            this.data = data;
        }
    }
}