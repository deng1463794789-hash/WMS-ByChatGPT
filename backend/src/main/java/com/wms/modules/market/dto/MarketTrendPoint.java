package com.wms.modules.market.dto;

public class MarketTrendPoint {
    private String time;
    private Double price;
    private Double averagePrice;
    private Long volume;
    private Double amount;

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    public Double getAveragePrice() { return averagePrice; }
    public void setAveragePrice(Double averagePrice) { this.averagePrice = averagePrice; }
    public Long getVolume() { return volume; }
    public void setVolume(Long volume) { this.volume = volume; }
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
}