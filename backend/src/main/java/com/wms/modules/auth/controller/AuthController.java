package com.wms.modules.auth.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import jakarta.validation.Valid;

import com.wms.common.annotation.AuditLog;
import com.wms.common.annotation.RateLimit;
import com.wms.common.api.ApiResponse;
import com.wms.common.security.SecurityUtils;
import com.wms.modules.auth.dto.LoginRequest;
import com.wms.modules.auth.dto.LoginResponse;
import com.wms.modules.auth.service.AuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    @RateLimit(maxAttempts = 5, windowSeconds = 300)
    @AuditLog(value = "用户登录", module = "认证模块")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/logout")
    @AuditLog(value = "用户登出", module = "认证模块")
    public ApiResponse<Void> logout() {
        authService.logout();
        return ApiResponse.success(null);
    }

    @GetMapping("/userinfo")
    public ApiResponse<Map<String, Object>> userinfo() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }
        return ApiResponse.success(authService.getUserInfo(userId));
    }

    @GetMapping("/menus")
    public ApiResponse<Object> menus() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ApiResponse.error(401, "未登录");
        }
        return ApiResponse.success(authService.getMenus(userId));
    }
}
