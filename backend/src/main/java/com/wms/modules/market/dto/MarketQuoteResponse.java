package com.wms.modules.market.dto;

import java.util.List;

public class MarketQuoteResponse {
    private String symbol;
    private String name;
    private String currency;
    private String marketState;
    private Double price;
    private Double previousClose;
    private Double open;
    private Double dayHigh;
    private Double dayLow;
    private Long volume;
    private Double change;
    private Double changePercent;
    private String quoteTime;
    private String source;
    private List<MarketTrendPoint> trendPoints;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getMarketState() { return marketState; }
    public void setMarketState(String marketState) { this.marketState = marketState; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getPreviousClose() { return previousClose; }
    public void setPreviousClose(Double previousClose) { this.previousClose = previousClose; }
    public Double getOpen() { return open; }
    public void setOpen(Double open) { this.open = open; }
    public Double getDayHigh() { return dayHigh; }
    public void setDayHigh(Double dayHigh) { this.dayHigh = dayHigh; }
    public Double getDayLow() { return dayLow; }
    public void setDayLow(Double dayLow) { this.dayLow = dayLow; }
    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }
    public Double getChange() { return change; }
    public void setChange(Double change) { this.change = change; }
    public Double getChangePercent() { return changePercent; }
    public void setChangePercent(Double changePercent) { this.changePercent = changePercent; }
    public String getQuoteTime() { return quoteTime; }
    public void setQuoteTime(String quoteTime) { this.quoteTime = quoteTime; }
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    public List<MarketTrendPoint> getTrendPoints() { return trendPoints; }
    public void setTrendPoints(List<MarketTrendPoint> trendPoints) { this.trendPoints = trendPoints; }
}