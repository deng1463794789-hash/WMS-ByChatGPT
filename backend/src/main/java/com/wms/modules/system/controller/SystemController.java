package com.wms.modules.system.controller;

import java.util.*;

import com.wms.common.annotation.AuditLog;
import com.wms.common.api.ApiResponse;
import com.wms.modules.system.service.SystemService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class SystemController {

    private final SystemService systemService;

    public SystemController(SystemService systemService) {
        this.systemService = systemService;
    }

    @GetMapping("/users")
    public ApiResponse<Map<String, Object>> userPage(@RequestParam(defaultValue = "1") long pageNum,
                                                      @RequestParam(defaultValue = "10") long pageSize,
                                                      @RequestParam(required = false) String keyword,
                                                      @RequestParam(required = false) String status) {
        List<Map<String, Object>> records = systemService.getUserList(pageNum, pageSize, keyword, status);
        long total = systemService.countUsers(keyword, status);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", total);
        result.put("pageNum", pageNum);
        result.put("pageSize", pageSize);
        return ApiResponse.success(result);
    }

    @PostMapping("/users")
    @AuditLog(value = "创建用户", module = "系统管理")
    public ApiResponse<Long> createUser(@RequestBody Map<String, Object> data) {
        return ApiResponse.success(systemService.createUser(data));
    }

    @PutMapping("/users/{id}")
    @AuditLog(value = "修改用户", module = "系统管理")
    public ApiResponse<Void> updateUser(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        systemService.updateUser(id, data);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/users/{id}")
    @AuditLog(value = "删除用户", module = "系统管理")
    public ApiResponse<Void> deleteUser(@PathVariable Long id) {
        systemService.deleteUser(id);
        return ApiResponse.success(null);
    }

    @PutMapping("/users/{id}/reset-password")
    @AuditLog(value = "重置密码", module = "系统管理")
    public ApiResponse<Void> resetPassword(@PathVariable Long id) {
        systemService.resetPassword(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/roles")
    public ApiResponse<Map<String, Object>> rolePage(@RequestParam(defaultValue = "1") long pageNum,
                                                      @RequestParam(defaultValue = "10") long pageSize,
                                                      @RequestParam(required = false) String keyword) {
        List<Map<String, Object>> records = systemService.getRoleList(pageNum, pageSize, keyword);
        long total = systemService.countRoles(keyword);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", total);
        return ApiResponse.success(result);
    }

    @GetMapping("/roles/all")
    public ApiResponse<List<Map<String, Object>>> allRoles() {
        return ApiResponse.success(systemService.getAllRoles());
    }

    @PostMapping("/roles")
    @AuditLog(value = "创建角色", module = "系统管理")
    public ApiResponse<Long> createRole(@RequestBody Map<String, Object> data) {
        return ApiResponse.success(systemService.createRole(data));
    }

    @PutMapping("/roles/{id}")
    @AuditLog(value = "修改角色", module = "系统管理")
    public ApiResponse<Void> updateRole(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        systemService.updateRole(id, data);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/roles/{id}")
    @AuditLog(value = "删除角色", module = "系统管理")
    public ApiResponse<Void> deleteRole(@PathVariable Long id) {
        systemService.deleteRole(id);
        return ApiResponse.success(null);
    }

    @GetMapping("/permissions/tree")
    public ApiResponse<List<Map<String, Object>>> permissionTree() {
        return ApiResponse.success(systemService.getPermissionTree());
    }

    @GetMapping("/system/setting")
    public ApiResponse<Map<String, Object>> getSetting() {
        return ApiResponse.success(systemService.getSetting());
    }

    @PutMapping("/system/setting")
    @AuditLog(value = "修改系统设置", module = "系统管理")
    public ApiResponse<Void> updateSetting(@RequestBody Map<String, Object> data) {
        systemService.updateSetting(data);
        return ApiResponse.success(null);
    }

    @GetMapping("/system/ping")
    public ApiResponse<Map<String, Object>> ping() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("service", "wms-backend");
        result.put("status", "UP");
        return ApiResponse.success(result);
    }
}
