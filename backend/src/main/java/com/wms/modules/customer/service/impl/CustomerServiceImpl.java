package com.wms.modules.customer.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.exception.BusinessException;
import com.wms.modules.customer.entity.Customer;
import com.wms.modules.customer.mapper.CustomerMapper;
import com.wms.modules.customer.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerServiceImpl implements CustomerService {

    private final CustomerMapper customerMapper;

    public CustomerServiceImpl(CustomerMapper customerMapper) {
        this.customerMapper = customerMapper;
    }

    @Override
    public List<Map<String, Object>> getList(long pageNum, long pageSize, String keyword) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>().orderByDesc(Customer::getId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Customer::getName, keyword).or().like(Customer::getCode, keyword));
        }
        Page<Customer> page = customerMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return page.getRecords().stream().map(this::toMap).collect(Collectors.toList());
    }

    @Override
    public long count(String keyword) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Customer::getName, keyword).or().like(Customer::getCode, keyword));
        }
        return customerMapper.selectCount(wrapper);
    }

    @Override
    public Map<String, Object> get(Long id) {
        return toMap(getOrThrow(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Map<String, Object> data) {
        Customer c = new Customer();
        c.setCode((String) data.get("code"));
        c.setName((String) data.get("name"));
        c.setContact((String) data.get("contact"));
        c.setPhone((String) data.get("phone"));
        c.setAddress((String) data.get("address"));
        c.setEmail((String) data.get("email"));
        c.setStatus((String) data.getOrDefault("status", "active"));
        customerMapper.insert(c);
        return c.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, Map<String, Object> data) {
        Customer c = getOrThrow(id);
        if (data.containsKey("code")) c.setCode((String) data.get("code"));
        if (data.containsKey("name")) c.setName((String) data.get("name"));
        if (data.containsKey("contact")) c.setContact((String) data.get("contact"));
        if (data.containsKey("phone")) c.setPhone((String) data.get("phone"));
        if (data.containsKey("address")) c.setAddress((String) data.get("address"));
        if (data.containsKey("email")) c.setEmail((String) data.get("email"));
        if (data.containsKey("status")) c.setStatus((String) data.get("status"));
        customerMapper.updateById(c);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getOrThrow(id);
        customerMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        customerMapper.deleteBatchIds(ids);
    }

    private Customer getOrThrow(Long id) {
        Customer c = customerMapper.selectById(id);
        if (c == null) throw new BusinessException("customer not found");
        return c;
    }

    private Map<String, Object> toMap(Customer c) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", c.getId());
        m.put("code", c.getCode());
        m.put("name", c.getName());
        m.put("contact", c.getContact());
        m.put("phone", c.getPhone());
        m.put("address", c.getAddress());
        m.put("email", c.getEmail());
        m.put("status", c.getStatus());
        m.put("createdAt", c.getCreatedAt());
        return m;
    }
}
