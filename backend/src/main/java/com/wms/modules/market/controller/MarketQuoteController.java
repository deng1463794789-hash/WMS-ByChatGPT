package com.wms.modules.market.controller;

import com.wms.common.api.ApiResponse;
import com.wms.modules.market.dto.MarketQuoteResponse;
import com.wms.modules.market.service.MarketQuoteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/market")
public class MarketQuoteController {

    private final MarketQuoteService marketQuoteService;

    public MarketQuoteController(MarketQuoteService marketQuoteService) {
        this.marketQuoteService = marketQuoteService;
    }

    @GetMapping("/quotes")
    public ApiResponse<List<MarketQuoteResponse>> quotes(@RequestParam(required = false) String symbols) {
        return ApiResponse.success(marketQuoteService.getQuotes(symbols));
    }
}