package com.wms.modules.employee.service;

import java.util.List;
import java.util.Map;

public interface EmployeeService {

    List<Map<String, Object>> getEmployeeList(long pageNum, long pageSize, String keyword, String department);

    long countEmployees(String keyword, String department);

    Map<String, Object> getEmployee(Long id);

    Long createEmployee(Map<String, Object> data);

    void updateEmployee(Long id, Map<String, Object> data);

    void deleteEmployee(Long id);

    void batchDelete(List<Long> ids);
}
