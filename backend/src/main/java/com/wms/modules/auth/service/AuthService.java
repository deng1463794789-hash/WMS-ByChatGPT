package com.wms.modules.auth.service;

import com.wms.common.api.ApiResponse;
import com.wms.modules.auth.dto.LoginRequest;
import com.wms.modules.auth.dto.LoginResponse;
import java.util.List;
import java.util.Map;

public interface AuthService {

    LoginResponse login(LoginRequest request);

    void logout();

    Map<String, Object> getUserInfo(Long userId);

    List<Map<String, Object>> getMenus(Long userId);
}
