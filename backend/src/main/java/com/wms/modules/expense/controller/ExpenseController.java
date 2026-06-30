package com.wms.modules.expense.controller;

import java.util.*;

import com.wms.common.annotation.AuditLog;
import com.wms.common.api.ApiResponse;
import com.wms.modules.expense.service.ExpenseService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> page(@RequestParam(defaultValue = "1") long pageNum,
                                                  @RequestParam(defaultValue = "10") long pageSize,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) String status,
                                                  @RequestParam(required = false) String type) {
        List<Map<String, Object>> records = expenseService.getExpenseList(pageNum, pageSize, keyword, status, type);
        long total = expenseService.countExpenses(keyword, status, type);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", total);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        return ApiResponse.success(expenseService.getExpense(id));
    }

    @PostMapping
    @AuditLog(value = "创建报销单", module = "报销管理")
    public ApiResponse<Long> create(@RequestBody Map<String, Object> data) {
        return ApiResponse.success(expenseService.createExpense(data));
    }

    @PutMapping("/{id}")
    @AuditLog(value = "修改报销单", module = "报销管理")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        expenseService.updateExpense(id, data);
        return ApiResponse.success(null);
    }

    @PutMapping("/{id}/approve")
    @AuditLog(value = "审批报销单", module = "报销管理")
    public ApiResponse<Void> approve(@PathVariable Long id, @RequestBody Map<String, String> body) {
        expenseService.approveExpense(id, body.get("action"), body.get("reason"));
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @AuditLog(value = "删除报销单", module = "报销管理")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        expenseService.deleteExpense(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/batch-delete")
    @AuditLog(value = "批量删除报销单", module = "报销管理")
    public ApiResponse<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        expenseService.batchDelete(body.get("ids"));
        return ApiResponse.success(null);
    }
}
