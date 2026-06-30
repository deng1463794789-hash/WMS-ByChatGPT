package com.wms.modules.system.entity;

import com.baomidou.mybatisplus.annotation.*;
import java.time.LocalDateTime;

@TableName("wms_system_setting")
public class SysSetting {

    @TableId(type = IdType.AUTO)
    private Long id;
    private String systemName;
    private String logo;
    private Integer passwordMinLength;
    private Integer loginMaxRetry;
    private Integer sessionTimeout;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSystemName() { return systemName; }
    public void setSystemName(String systemName) { this.systemName = systemName; }
    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }
    public Integer getPasswordMinLength() { return passwordMinLength; }
    public void setPasswordMinLength(Integer passwordMinLength) { this.passwordMinLength = passwordMinLength; }
    public Integer getLoginMaxRetry() { return loginMaxRetry; }
    public void setLoginMaxRetry(Integer loginMaxRetry) { this.loginMaxRetry = loginMaxRetry; }
    public Integer getSessionTimeout() { return sessionTimeout; }
    public void setSessionTimeout(Integer sessionTimeout) { this.sessionTimeout = sessionTimeout; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
