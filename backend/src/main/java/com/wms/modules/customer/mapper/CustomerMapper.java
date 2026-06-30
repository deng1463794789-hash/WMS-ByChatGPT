package com.wms.modules.customer.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.customer.entity.Customer;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface CustomerMapper extends BaseMapper<Customer> {
}
