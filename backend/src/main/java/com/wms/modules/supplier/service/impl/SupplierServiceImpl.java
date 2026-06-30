package com.wms.modules.supplier.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.exception.BusinessException;
import com.wms.modules.supplier.entity.Supplier;
import com.wms.modules.supplier.mapper.SupplierMapper;
import com.wms.modules.supplier.service.SupplierService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SupplierServiceImpl implements SupplierService {

    private final SupplierMapper supplierMapper;

    public SupplierServiceImpl(SupplierMapper supplierMapper) {
        this.supplierMapper = supplierMapper;
    }

    @Override
    public List<Map<String, Object>> getList(long pageNum, long pageSize, String keyword) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<Supplier>().orderByDesc(Supplier::getId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Supplier::getName, keyword).or().like(Supplier::getCode, keyword));
        }
        Page<Supplier> page = supplierMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return page.getRecords().stream().map(this::toMap).collect(Collectors.toList());
    }

    @Override
    public long count(String keyword) {
        LambdaQueryWrapper<Supplier> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Supplier::getName, keyword).or().like(Supplier::getCode, keyword));
        }
        return supplierMapper.selectCount(wrapper);
    }

    @Override
    public Map<String, Object> get(Long id) {
        return toMap(getOrThrow(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Map<String, Object> data) {
        Supplier s = new Supplier();
        s.setCode((String) data.get("code"));
        s.setName((String) data.get("name"));
        s.setContact((String) data.get("contact"));
        s.setPhone((String) data.get("phone"));
        s.setAddress((String) data.get("address"));
        s.setEmail((String) data.get("email"));
        s.setStatus((String) data.getOrDefault("status", "active"));
        supplierMapper.insert(s);
        return s.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, Map<String, Object> data) {
        Supplier s = getOrThrow(id);
        if (data.containsKey("code")) s.setCode((String) data.get("code"));
        if (data.containsKey("name")) s.setName((String) data.get("name"));
        if (data.containsKey("contact")) s.setContact((String) data.get("contact"));
        if (data.containsKey("phone")) s.setPhone((String) data.get("phone"));
        if (data.containsKey("address")) s.setAddress((String) data.get("address"));
        if (data.containsKey("email")) s.setEmail((String) data.get("email"));
        if (data.containsKey("status")) s.setStatus((String) data.get("status"));
        supplierMapper.updateById(s);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getOrThrow(id);
        supplierMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        supplierMapper.deleteBatchIds(ids);
    }

    private Supplier getOrThrow(Long id) {
        Supplier s = supplierMapper.selectById(id);
        if (s == null) throw new BusinessException("supplier not found");
        return s;
    }

    private Map<String, Object> toMap(Supplier s) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", s.getId());
        m.put("code", s.getCode());
        m.put("name", s.getName());
        m.put("contact", s.getContact());
        m.put("phone", s.getPhone());
        m.put("address", s.getAddress());
        m.put("email", s.getEmail());
        m.put("status", s.getStatus());
        m.put("createdAt", s.getCreatedAt());
        return m;
    }
}
