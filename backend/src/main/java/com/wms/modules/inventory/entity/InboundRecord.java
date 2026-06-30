package com.wms.modules.inventory.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@TableName("wms_inbound")
public class InboundRecord {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String inboundNo;
    private String type;
    private Integer productCount;
    private Integer totalQuantity;
    private String operator;
    private LocalDateTime inboundTime;
    private String remark;
    private String status;

    @TableLogic
    private Integer isDeleted;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    @TableField(exist = false)
    private List<InboundItem> items;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInboundNo() { return inboundNo; }
    public void setInboundNo(String inboundNo) { this.inboundNo = inboundNo; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public Integer getProductCount() { return productCount; }
    public void setProductCount(Integer productCount) { this.productCount = productCount; }
    public Integer getTotalQuantity() { return totalQuantity; }
    public void setTotalQuantity(Integer totalQuantity) { this.totalQuantity = totalQuantity; }
    public String getOperator() { return operator; }
    public void setOperator(String operator) { this.operator = operator; }
    public LocalDateTime getInboundTime() { return inboundTime; }
    public void setInboundTime(LocalDateTime inboundTime) { this.inboundTime = inboundTime; }
    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Integer getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Integer isDeleted) { this.isDeleted = isDeleted; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public List<InboundItem> getItems() { return items; }
    public void setItems(List<InboundItem> items) { this.items = items; }
}
