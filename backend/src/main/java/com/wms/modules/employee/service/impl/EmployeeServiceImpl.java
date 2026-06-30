package com.wms.modules.employee.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.exception.BusinessException;
import com.wms.modules.employee.entity.Employee;
import com.wms.modules.employee.mapper.EmployeeMapper;
import com.wms.modules.employee.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeMapper employeeMapper;

    public EmployeeServiceImpl(EmployeeMapper employeeMapper) {
        this.employeeMapper = employeeMapper;
    }

    @Override
    public List<Map<String, Object>> getEmployeeList(long pageNum, long pageSize, String keyword, String department) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<Employee>().orderByDesc(Employee::getId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Employee::getName, keyword).or().like(Employee::getCode, keyword));
        }
        if (department != null && !department.isEmpty()) {
            wrapper.eq(Employee::getDepartment, department);
        }
        Page<Employee> page = employeeMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return page.getRecords().stream().map(this::toMap).collect(Collectors.toList());
    }

    @Override
    public long countEmployees(String keyword, String department) {
        LambdaQueryWrapper<Employee> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Employee::getName, keyword).or().like(Employee::getCode, keyword));
        }
        if (department != null && !department.isEmpty()) {
            wrapper.eq(Employee::getDepartment, department);
        }
        return employeeMapper.selectCount(wrapper);
    }

    @Override
    public Map<String, Object> getEmployee(Long id) {
        return toMap(getOrThrow(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createEmployee(Map<String, Object> data) {
        Employee e = new Employee();
        if (data.get("code") == null) {
            e.setCode("EMP" + System.currentTimeMillis() % 100000);
        } else {
            e.setCode((String) data.get("code"));
        }
        e.setName((String) data.get("name"));
        e.setGender((String) data.getOrDefault("gender", "male"));
        e.setPhone((String) data.get("phone"));
        e.setDepartment((String) data.get("department"));
        e.setPosition((String) data.get("position"));
        if (data.containsKey("hireDate") && data.get("hireDate") != null) {
            e.setHireDate(java.time.LocalDate.parse(((String) data.get("hireDate"))));
        }
        if (data.containsKey("salary")) e.setSalary(new java.math.BigDecimal(data.get("salary").toString()));
        e.setRemark((String) data.get("remark"));
        e.setStatus((String) data.getOrDefault("status", "active"));
        employeeMapper.insert(e);
        return e.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateEmployee(Long id, Map<String, Object> data) {
        Employee e = getOrThrow(id);
        if (data.containsKey("name")) e.setName((String) data.get("name"));
        if (data.containsKey("gender")) e.setGender((String) data.get("gender"));
        if (data.containsKey("phone")) e.setPhone((String) data.get("phone"));
        if (data.containsKey("department")) e.setDepartment((String) data.get("department"));
        if (data.containsKey("position")) e.setPosition((String) data.get("position"));
        if (data.containsKey("hireDate") && data.get("hireDate") != null) {
            e.setHireDate(java.time.LocalDate.parse(((String) data.get("hireDate"))));
        }
        if (data.containsKey("salary")) e.setSalary(new java.math.BigDecimal(data.get("salary").toString()));
        if (data.containsKey("remark")) e.setRemark((String) data.get("remark"));
        if (data.containsKey("status")) e.setStatus((String) data.get("status"));
        employeeMapper.updateById(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteEmployee(Long id) {
        getOrThrow(id);
        employeeMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        employeeMapper.deleteBatchIds(ids);
    }

    private Employee getOrThrow(Long id) {
        Employee e = employeeMapper.selectById(id);
        if (e == null) throw new BusinessException("employee not found");
        return e;
    }

    private Map<String, Object> toMap(Employee e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", e.getId());
        m.put("code", e.getCode());
        m.put("name", e.getName());
        m.put("gender", e.getGender());
        m.put("phone", e.getPhone());
        m.put("department", e.getDepartment());
        m.put("position", e.getPosition());
        m.put("hireDate", e.getHireDate());
        m.put("salary", e.getSalary());
        m.put("status", e.getStatus());
        m.put("remark", e.getRemark());
        m.put("createdAt", e.getCreatedAt());
        return m;
    }
}
