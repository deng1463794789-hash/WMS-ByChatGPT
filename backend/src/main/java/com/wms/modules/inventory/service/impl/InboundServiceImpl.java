package com.wms.modules.inventory.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.exception.BusinessException;
import com.wms.modules.inventory.entity.InboundItem;
import com.wms.modules.inventory.entity.InboundRecord;
import com.wms.modules.inventory.mapper.InboundItemMapper;
import com.wms.modules.inventory.mapper.InboundRecordMapper;
import com.wms.modules.inventory.service.InboundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InboundServiceImpl extends ServiceImpl<InboundRecordMapper, InboundRecord> implements InboundService {

    private final InboundItemMapper inboundItemMapper;

    public InboundServiceImpl(InboundItemMapper inboundItemMapper) {
        this.inboundItemMapper = inboundItemMapper;
    }

    @Override
    public IPage<InboundRecord> pageRecords(long pageNum, long pageSize, String inboundNo, String type) {
        Page<InboundRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<InboundRecord> wrapper = new LambdaQueryWrapper<InboundRecord>()
                .orderByDesc(InboundRecord::getId);
        if (inboundNo != null && !inboundNo.isEmpty()) {
            wrapper.like(InboundRecord::getInboundNo, inboundNo);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(InboundRecord::getType, type);
        }
        return this.page(page, wrapper);
    }

    @Override
    public InboundRecord getRecord(Long id) {
        return getByIdOrThrow(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRecord(InboundRecord record, List<InboundItem> items) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        record.setInboundNo("IN-" + dateStr + "-" + String.format("%03d", System.currentTimeMillis() % 1000));
        int productCount = items != null ? items.size() : 0;
        int totalQuantity = items != null ? items.stream().mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum() : 0;
        record.setProductCount(productCount);
        record.setTotalQuantity(totalQuantity);
        record.setStatus("completed");
        if (record.getInboundTime() == null) {
            record.setInboundTime(LocalDateTime.now());
        }
        if (record.getOperator() == null) {
            record.setOperator("admin");
        }
        save(record);

        if (items != null) {
            for (InboundItem item : items) {
                item.setInboundId(record.getId());
                inboundItemMapper.insert(item);
            }
        }
        return record.getId();
    }

    @Override
    public InboundRecord getDetail(Long id) {
        InboundRecord record = getByIdOrThrow(id);
        List<InboundItem> items = inboundItemMapper.selectList(
                new LambdaQueryWrapper<InboundItem>().eq(InboundItem::getInboundId, id));
        record.setItems(items);
        return record;
    }

    private InboundRecord getByIdOrThrow(Long id) {
        InboundRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("inbound record not found");
        }
        return record;
    }
}
