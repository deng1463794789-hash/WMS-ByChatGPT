package com.wms.modules.expense.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wms.common.exception.BusinessException;
import com.wms.modules.expense.entity.Expense;
import com.wms.modules.expense.mapper.ExpenseMapper;
import com.wms.modules.expense.service.ExpenseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private final ExpenseMapper expenseMapper;

    public ExpenseServiceImpl(ExpenseMapper expenseMapper) {
        this.expenseMapper = expenseMapper;
    }

    @Override
    public List<Map<String, Object>> getExpenseList(long pageNum, long pageSize, String keyword, String status, String type) {
        LambdaQueryWrapper<Expense> wrapper = new LambdaQueryWrapper<Expense>().orderByDesc(Expense::getId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Expense::getExpenseNo, keyword).or().like(Expense::getApplicant, keyword));
        }
        if (status != null && !status.isEmpty()) {
            wrapper.eq(Expense::getStatus, status);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq(Expense::getType, type);
        }
        Page<Expense> page = expenseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return page.getRecords().stream().map(this::toMap).collect(Collectors.toList());
    }

    @Override
    public long countExpenses(String keyword, String status, String type) {
        LambdaQueryWrapper<Expense> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Expense::getExpenseNo, keyword).or().like(Expense::getApplicant, keyword));
        }
        if (status != null && !status.isEmpty()) wrapper.eq(Expense::getStatus, status);
        if (type != null && !type.isEmpty()) wrapper.eq(Expense::getType, type);
        return expenseMapper.selectCount(wrapper);
    }

    @Override
    public Map<String, Object> getExpense(Long id) {
        return toMap(getOrThrow(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createExpense(Map<String, Object> data) {
        Expense e = new Expense();
        e.setExpenseNo("EXP-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + "-" + String.format("%03d", System.currentTimeMillis() % 1000));
        e.setType((String) data.get("type"));
        e.setAmount(new BigDecimal(data.get("amount").toString()));
        e.setApplicant((String) data.get("applicant"));
        e.setDepartment((String) data.get("department"));
        if (data.containsKey("expenseDate") && data.get("expenseDate") != null) {
            e.setExpenseDate(java.time.LocalDate.parse(((String) data.get("expenseDate"))));
        }
        e.setInvoiceNo((String) data.get("invoiceNo"));
        e.setReason((String) data.get("reason"));
        e.setRemark((String) data.get("remark"));
        e.setStatus("pending");
        expenseMapper.insert(e);
        return e.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateExpense(Long id, Map<String, Object> data) {
        Expense e = getOrThrow(id);
        if (data.containsKey("type")) e.setType((String) data.get("type"));
        if (data.containsKey("amount")) e.setAmount(new BigDecimal(data.get("amount").toString()));
        if (data.containsKey("applicant")) e.setApplicant((String) data.get("applicant"));
        if (data.containsKey("department")) e.setDepartment((String) data.get("department"));
        if (data.containsKey("invoiceNo")) e.setInvoiceNo((String) data.get("invoiceNo"));
        if (data.containsKey("reason")) e.setReason((String) data.get("reason"));
        if (data.containsKey("remark")) e.setRemark((String) data.get("remark"));
        if (data.containsKey("expenseDate") && data.get("expenseDate") != null) {
            e.setExpenseDate(java.time.LocalDate.parse(((String) data.get("expenseDate"))));
        }
        expenseMapper.updateById(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteExpense(Long id) {
        getOrThrow(id);
        expenseMapper.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveExpense(Long id, String action, String reason) {
        Expense e = getOrThrow(id);
        if ("approve".equals(action)) {
            e.setStatus("approved");
            e.setApprover("admin");
            e.setApproveTime(LocalDateTime.now());
        } else if ("reject".equals(action)) {
            e.setStatus("rejected");
            e.setRejectReason(reason);
        }
        expenseMapper.updateById(e);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchDelete(List<Long> ids) {
        expenseMapper.deleteBatchIds(ids);
    }

    private Expense getOrThrow(Long id) {
        Expense e = expenseMapper.selectById(id);
        if (e == null) throw new BusinessException("expense not found");
        return e;
    }

    private Map<String, Object> toMap(Expense e) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", e.getId());
        m.put("expenseNo", e.getExpenseNo());
        m.put("type", e.getType());
        m.put("amount", e.getAmount());
        m.put("applicant", e.getApplicant());
        m.put("department", e.getDepartment());
        m.put("expenseDate", e.getExpenseDate());
        m.put("invoiceNo", e.getInvoiceNo());
        m.put("reason", e.getReason());
        m.put("status", e.getStatus());
        m.put("approver", e.getApprover());
        m.put("approveTime", e.getApproveTime());
        m.put("rejectReason", e.getRejectReason());
        m.put("remark", e.getRemark());
        m.put("createdAt", e.getCreatedAt());
        return m;
    }
}
