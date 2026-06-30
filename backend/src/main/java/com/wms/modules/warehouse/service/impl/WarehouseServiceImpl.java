package com.wms.modules.warehouse.service.impl;

import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.exception.BusinessException;
import com.wms.modules.warehouse.entity.Warehouse;
import com.wms.modules.warehouse.mapper.WarehouseMapper;
import com.wms.modules.warehouse.service.WarehouseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WarehouseServiceImpl implements WarehouseService {

    private final WarehouseMapper warehouseMapper;

    public WarehouseServiceImpl(WarehouseMapper warehouseMapper) {
        this.warehouseMapper = warehouseMapper;
    }

    @Override
    public List<Map<String, Object>> getList(long pageNum, long pageSize, String keyword) {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<Warehouse>().orderByDesc(Warehouse::getId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Warehouse::getName, keyword).or().like(Warehouse::getCode, keyword));
        }
        Page<Warehouse> page = warehouseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return page.getRecords().stream().map(this::toMap).collect(Collectors.toList());
    }

    @Override
    public long count(String keyword) {
        LambdaQueryWrapper<Warehouse> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Warehouse::getName, keyword).or().like(Warehouse::getCode, keyword));
        }
        return warehouseMapper.selectCount(wrapper);
    }

    @Override
    public Map<String, Object> get(Long id) {
        return toMap(getOrThrow(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Map<String, Object> data) {
        Warehouse w = new Warehouse();
        w.setCode((String) data.get("code"));
        w.setName((String) data.get("name"));
        w.setAddress((String) data.get("address"));
        w.setManager((String) data.get("manager"));
        w.setPhone((String) data.get("phone"));
        if (data.containsKey("area")) w.setArea(new java.math.BigDecimal(data.get("area").toString()));
        w.setStatus((String) data.getOrDefault("status", "active"));
        warehouseMapper.insert(w);
        return w.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, Map<String, Object> data) {
        Warehouse w = getOrThrow(id);
        if (data.containsKey("code")) w.setCode((String) data.get("code"));
        if (data.containsKey("name")) w.setName((String) data.get("name"));
        if (data.containsKey("address")) w.setAddress((String) data.get("address"));
        if (data.containsKey("manager")) w.setManager((String) data.get("manager"));
        if (data.containsKey("phone")) w.setPhone((String) data.get("phone"));
        if (data.containsKey("area")) w.setArea(new java.math.BigDecimal(data.get("area").toString()));
        if (data.containsKey("status")) w.setStatus((String) data.get("status"));
        warehouseMapper.updateById(w);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        getOrThrow(id);
        warehouseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        warehouseMapper.deleteBatchIds(ids);
    }

    private Warehouse getOrThrow(Long id) {
        Warehouse w = warehouseMapper.selectById(id);
        if (w == null) throw new BusinessException("warehouse not found");
        return w;
    }

    private Map<String, Object> toMap(Warehouse w) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", w.getId());
        m.put("code", w.getCode());
        m.put("name", w.getName());
        m.put("address", w.getAddress());
        m.put("manager", w.getManager());
        m.put("phone", w.getPhone());
        m.put("area", w.getArea());
        m.put("status", w.getStatus());
        m.put("createdAt", w.getCreatedAt());
        return m;
    }
}
