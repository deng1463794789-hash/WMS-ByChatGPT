package com.wms.modules.inventory.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.modules.inventory.entity.InboundItem;
import com.wms.modules.inventory.entity.InboundRecord;

public interface InboundService extends IService<InboundRecord> {

    IPage<InboundRecord> pageRecords(long pageNum, long pageSize, String inboundNo, String type);

    InboundRecord getRecord(Long id);

    Long createRecord(InboundRecord record, List<InboundItem> items);

    InboundRecord getDetail(Long id);
}
