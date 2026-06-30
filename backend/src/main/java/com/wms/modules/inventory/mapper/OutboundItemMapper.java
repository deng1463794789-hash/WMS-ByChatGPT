package com.wms.modules.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.inventory.entity.OutboundItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OutboundItemMapper extends BaseMapper<OutboundItem> {
}
