package com.wms.modules.employee.controller;

import java.util.*;

import com.wms.common.annotation.AuditLog;
import com.wms.common.api.ApiResponse;
import com.wms.modules.employee.service.EmployeeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @GetMapping
    public ApiResponse<Map<String, Object>> page(@RequestParam(defaultValue = "1") long pageNum,
                                                  @RequestParam(defaultValue = "10") long pageSize,
                                                  @RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) String department) {
        List<Map<String, Object>> records = employeeService.getEmployeeList(pageNum, pageSize, keyword, department);
        long total = employeeService.countEmployees(keyword, department);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("records", records);
        result.put("total", total);
        return ApiResponse.success(result);
    }

    @GetMapping("/{id}")
    public ApiResponse<Map<String, Object>> detail(@PathVariable Long id) {
        return ApiResponse.success(employeeService.getEmployee(id));
    }

    @PostMapping
    @AuditLog(value = "创建员工", module = "员工管理")
    public ApiResponse<Long> create(@RequestBody Map<String, Object> data) {
        return ApiResponse.success(employeeService.createEmployee(data));
    }

    @PutMapping("/{id}")
    @AuditLog(value = "修改员工", module = "员工管理")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Map<String, Object> data) {
        employeeService.updateEmployee(id, data);
        return ApiResponse.success(null);
    }

    @DeleteMapping("/{id}")
    @AuditLog(value = "删除员工", module = "员工管理")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return ApiResponse.success(null);
    }

    @PostMapping("/batch-delete")
    @AuditLog(value = "批量删除员工", module = "员工管理")
    public ApiResponse<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        employeeService.batchDelete(body.get("ids"));
        return ApiResponse.success(null);
    }
}
