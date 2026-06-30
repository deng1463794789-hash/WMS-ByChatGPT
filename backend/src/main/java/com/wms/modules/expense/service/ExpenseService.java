package com.wms.modules.expense.service;

import java.util.List;
import java.util.Map;

public interface ExpenseService {

    List<Map<String, Object>> getExpenseList(long pageNum, long pageSize, String keyword, String status, String type);

    long countExpenses(String keyword, String status, String type);

    Map<String, Object> getExpense(Long id);

    Long createExpense(Map<String, Object> data);

    void updateExpense(Long id, Map<String, Object> data);

    void deleteExpense(Long id);

    void approveExpense(Long id, String action, String reason);

    void batchDelete(List<Long> ids);
}
