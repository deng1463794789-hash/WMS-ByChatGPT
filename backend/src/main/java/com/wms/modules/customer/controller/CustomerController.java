package com.wms.modules.customer.controller;

import java.util.*;

import com.wms.common.annotation.AuditLog;
import com.wms.common.api.ApiResponse;
import com.wms.modules.customer.service.CustomerService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> page(@RequestParam(defaultValue = "1") long pageNum,
                                                  @RequestParam(defaultValue = "10") long pageSize,
                                                  @RequestParam(required = false) String keyword) {
        List<Map<String, Object>> records = customerService.getList(pageNum, pageSize, keyword);
        long total = customerService.count(keyword);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", total);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        return ApiResponse.success(customerService.get(id));
    }

    @PostMapping
    @AuditLog(value = "创建客户", module = "客户管理")
    public ApiResponse<Long> create(@RequestBody Map<String, Object> data) {
        return ApiResponse.success(customerService.create(data));
    }

    @PutMapping("/{id}")
    @AuditLog(value = "修改客户", module = "客户管理")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        customerService.update(id, data);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @AuditLog(value = "删除客户", module = "客户管理")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        customerService.delete(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/batch-delete")
    @AuditLog(value = "批量删除客户", module = "客户管理")
    public ApiResponse<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        customerService.batchDelete(body.get("ids"));
        return ApiResponse.success(null);
    }
}
