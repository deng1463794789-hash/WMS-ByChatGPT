package com.wms.modules.auth.dto;

import java.util.List;
import java.util.Map;

public class LoginResponse {

    private String token;
    private String refreshToken;
    private Map<String, Object> userInfo;
    private List<Map<String, Object>> menus;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public Map<String, Object> getUserInfo() { return userInfo; }
    public void setUserInfo(Map<String, Object> userInfo) { this.userInfo = userInfo; }
    public List<Map<String, Object>> getMenus() { return menus; }
    public void setMenus(List<Map<String, Object>> menus) { this.menus = menus; }
}
