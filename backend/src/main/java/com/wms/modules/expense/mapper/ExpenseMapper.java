package com.wms.modules.expense.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wms.modules.expense.entity.Expense;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ExpenseMapper extends BaseMapper<Expense> {
}
