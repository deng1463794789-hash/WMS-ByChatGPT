package com.localwarehouse.backend.module.system.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.localwarehouse.backend.common.api.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/system")
public class SystemController {

    @GetMapping("/ping")
    public ApiResponse<Map<String, Object>> ping() {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("service", "local-warehouse-backend");
        result.put("status", "UP");
        return ApiResponse.success(result);
    }
}
