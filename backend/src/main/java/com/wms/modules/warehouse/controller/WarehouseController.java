package com.wms.modules.warehouse.controller;

import java.util.*;

import com.wms.common.annotation.AuditLog;
import com.wms.common.api.ApiResponse;
import com.wms.modules.warehouse.service.WarehouseService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/warehouses")
public class WarehouseController {

    private final WarehouseService warehouseService;

    public WarehouseController(WarehouseService warehouseService) {
        this.warehouseService = warehouseService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> page(@RequestParam(defaultValue = "1") long pageNum,
                                                  @RequestParam(defaultValue = "10") long pageSize,
                                                  @RequestParam(required = false) String keyword) {
        List<Map<String, Object>> records = warehouseService.getList(pageNum, pageSize, keyword);
        long total = warehouseService.count(keyword);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", total);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        return ApiResponse.success(warehouseService.get(id));
    }

    @PostMapping
    @AuditLog(value = "创建仓库", module = "仓库管理")
    public ApiResponse<Long> create(@RequestBody Map<String, Object> data) {
        return ApiResponse.success(warehouseService.create(data));
    }

    @PutMapping("/{id}")
    @AuditLog(value = "修改仓库", module = "仓库管理")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        warehouseService.update(id, data);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @AuditLog(value = "删除仓库", module = "仓库管理")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        warehouseService.delete(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/batch-delete")
    @AuditLog(value = "批量删除仓库", module = "仓库管理")
    public ApiResponse<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        warehouseService.batchDelete(body.get("ids"));
        return ApiResponse.success(null);
    }
}
