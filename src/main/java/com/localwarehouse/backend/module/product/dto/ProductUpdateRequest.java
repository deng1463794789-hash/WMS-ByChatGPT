package com.localwarehouse.backend.module.product.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ProductUpdateRequest {

    @NotBlank(message = "sku cannot be blank")
    @Size(max = 64, message = "sku length must be <= 64")
    private String sku;

    @NotBlank(message = "name cannot be blank")
    @Size(max = 128, message = "name length must be <= 128")
    private String name;

    @NotBlank(message = "unit cannot be blank")
    @Size(max = 32, message = "unit length must be <= 32")
    private String unit;

    @NotNull(message = "stockQuantity cannot be null")
    @Min(value = 0, message = "stockQuantity must be >= 0")
    private Integer stockQuantity;

    @NotNull(message = "safeStock cannot be null")
    @Min(value = 0, message = "safeStock must be >= 0")
    private Integer safeStock;

    @Size(max = 255, message = "remark length must be <= 255")
    private String remark;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Integer getSafeStock() {
        return safeStock;
    }

    public void setSafeStock(Integer safeStock) {
        this.safeStock = safeStock;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
