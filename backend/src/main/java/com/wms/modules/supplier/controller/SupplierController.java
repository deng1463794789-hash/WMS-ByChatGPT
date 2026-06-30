package com.wms.modules.supplier.controller;

import java.util.*;

import com.wms.common.annotation.AuditLog;
import com.wms.common.api.ApiResponse;
import com.wms.modules.supplier.service.SupplierService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/suppliers")
public class SupplierController {

    private final SupplierService supplierService;

    public SupplierController(SupplierService supplierService) {
        this.supplierService = supplierService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> page(@RequestParam(defaultValue = "1") long pageNum,
                                                  @RequestParam(defaultValue = "10") long pageSize,
                                                  @RequestParam(required = false) String keyword) {
        List<Map<String, Object>> records = supplierService.getList(pageNum, pageSize, keyword);
        long total = supplierService.count(keyword);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", total);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        return ApiResponse.success(supplierService.get(id));
    }

    @PostMapping
    @AuditLog(value = "创建供应商", module = "供应商管理")
    public ApiResponse<Long> create(@RequestBody Map<String, Object> data) {
        return ApiResponse.success(supplierService.create(data));
    }

    @PutMapping("/{id}")
    @AuditLog(value = "修改供应商", module = "供应商管理")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        supplierService.update(id, data);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @AuditLog(value = "删除供应商", module = "供应商管理")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        supplierService.delete(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/batch-delete")
    @AuditLog(value = "批量删除供应商", module = "供应商管理")
    public ApiResponse<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        supplierService.batchDelete(body.get("ids"));
        return ApiResponse.success(null);
    }
}
