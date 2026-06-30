package com.wms.modules.supplier.service;

import java.util.List;
import java.util.Map;

public interface SupplierService {

    List<Map<String, Object>> getList(long pageNum, long pageSize, String keyword);

    long count(String keyword);

    Map<String, Object> get(Long id);

    Long create(Map<String, Object> data);

    void update(Long id, Map<String, Object> data);

    void delete(Long id);

    void batchDelete(List<Long> ids);
}
