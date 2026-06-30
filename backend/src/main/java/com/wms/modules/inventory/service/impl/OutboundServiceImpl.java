package com.wms.modules.inventory.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.exception.BusinessException;
import com.wms.modules.inventory.entity.OutboundItem;
import com.wms.modules.inventory.entity.OutboundRecord;
import com.wms.modules.inventory.mapper.OutboundItemMapper;
import com.wms.modules.inventory.mapper.OutboundRecordMapper;
import com.wms.modules.inventory.service.OutboundService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OutboundServiceImpl extends ServiceImpl<OutboundRecordMapper, OutboundRecord> implements OutboundService {

    private final OutboundItemMapper outboundItemMapper;

    public OutboundServiceImpl(OutboundItemMapper outboundItemMapper) {
        this.outboundItemMapper = outboundItemMapper;
    }

    @Override
    public IPage<OutboundRecord> pageRecords(long pageNum, long pageSize, String outboundNo, String type) {
        Page<OutboundRecord> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<OutboundRecord> wrapper = new LambdaQueryWrapper<OutboundRecord>()
                .orderByDesc(OutboundRecord::getId);
        if (outboundNo != null && !outboundNo.isEmpty()) {
            wrapper.like(OutboundRecord::getOutboundNo, outboundNo);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(OutboundRecord::getType, type);
        }
        return this.page(page, wrapper);
    }

    @Override
    public OutboundRecord getRecord(Long id) {
        return getByIdOrThrow(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createRecord(OutboundRecord record, List<OutboundItem> items) {
        String dateStr = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        record.setOutboundNo("OUT-" + dateStr + "-" + String.format("%03d", (System.nanoTime() / 1000) % 1000));
        int productCount = items != null ? items.size() : 0;
        int totalQuantity = items != null ? items.stream().mapToInt(i -> i.getQuantity() != null ? i.getQuantity() : 0).sum() : 0;
        record.setProductCount(productCount);
        record.setTotalQuantity(totalQuantity);
        record.setStatus("completed");
        if (record.getOutboundTime() == null) {
            record.setOutboundTime(LocalDateTime.now());
        }
        if (record.getOperator() == null) {
            record.setOperator("admin");
        }
        save(record);

        if (items != null) {
            for (OutboundItem item : items) {
                item.setOutboundId(record.getId());
                outboundItemMapper.insert(item);
            }
        }
        return record.getId();
    }

    @Override
    public OutboundRecord getDetail(Long id) {
        OutboundRecord record = getByIdOrThrow(id);
        List<OutboundItem> items = outboundItemMapper.selectList(
                new LambdaQueryWrapper<OutboundItem>().eq(OutboundItem::getOutboundId, id));
        record.setItems(items);
        return record;
    }

    private OutboundRecord getByIdOrThrow(Long id) {
        OutboundRecord record = getById(id);
        if (record == null) {
            throw new BusinessException("outbound record not found");
        }
        return record;
    }
}
