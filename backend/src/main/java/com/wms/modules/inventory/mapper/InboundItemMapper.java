package com.wms.modules.inventory.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.inventory.entity.InboundItem;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface InboundItemMapper extends BaseMapper<InboundItem> {
}
