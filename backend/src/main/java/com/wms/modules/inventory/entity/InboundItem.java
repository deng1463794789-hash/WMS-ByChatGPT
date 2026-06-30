package com.wms.modules.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.math.BigDecimal;

@TableName("wms_inbound_item")
public class InboundItem {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long inboundId;
    private Long productId;
    private String productName;
    private String sku;
    private Integer quantity;
    private BigDecimal price;
    private String remark;

    @TableLogic
    private Integer isDeleted;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getInboundId() { return inboundId; }
    public void setInboundId(Long inboundId) { this.inboundId = inboundId; }
    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
}
