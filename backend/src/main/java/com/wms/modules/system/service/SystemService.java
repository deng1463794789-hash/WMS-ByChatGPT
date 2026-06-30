package com.wms.modules.system.service;

import java.util.List;
import java.util.Map;

public interface SystemService {

    List<Map<String, Object>> getUserList(long pageNum, long pageSize, String keyword, String status);

    long countUsers(String keyword, String status);

    Map<String, Object> getUser(Long id);

    Long createUser(Map<String, Object> data);

    void updateUser(Long id, Map<String, Object> data);

    void deleteUser(Long id);

    void resetPassword(Long id);

    List<Map<String, Object>> getRoleList(long pageNum, long pageSize, String keyword);

    long countRoles(String keyword);

    Map<String, Object> getRole(Long id);

    Long createRole(Map<String, Object> data);

    void updateRole(Long id, Map<String, Object> data);

    void deleteRole(Long id);

    List<Map<String, Object>> getAllRoles();

    List<Map<String, Object>> getPermissionTree();

    Map<String, Object> getSetting();

    void updateSetting(Map<String, Object> data);
}
