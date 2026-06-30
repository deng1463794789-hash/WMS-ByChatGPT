package com.wms.modules.inventory.service;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.modules.inventory.entity.OutboundItem;
import com.wms.modules.inventory.entity.OutboundRecord;

public interface OutboundService extends IService<OutboundRecord> {

    IPage<OutboundRecord> pageRecords(long pageNum, long pageSize, String outboundNo, String type);

    OutboundRecord getRecord(Long id);

    Long createRecord(OutboundRecord record, List<OutboundItem> items);

    OutboundRecord getDetail(Long id);
}
