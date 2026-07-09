package com.wms.modules.product.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wms.modules.product.entity.Product;
import com.wms.modules.product.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductCrudPersistenceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductMapper productMapper;

    @Test
    void productCrudShouldReachBackendAndPersist() throws Exception {
        String token = loginAndGetToken();

        Map<String, Object> createBody = new LinkedHashMap<>();
        createBody.put("sku", "TEST-PERSIST-001");
        createBody.put("name", "Persistence Test Product");
        createBody.put("unit", "pcs");
        createBody.put("stockQuantity", 10);
        createBody.put("safeStock", 2);
        createBody.put("price", new BigDecimal("9.99"));
        createBody.put("status", "active");

        MvcResult createResult = mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        Long id = objectMapper.readTree(createResult.getResponse().getContentAsString()).path("data").asLong();
        Product inserted = productMapper.selectById(id);
        assertNotNull(inserted);
        assertEquals("TEST-PERSIST-001", inserted.getSku());

        mockMvc.perform(get("/api/products/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.sku").value("TEST-PERSIST-001"));

        Map<String, Object> updateBody = new LinkedHashMap<>(createBody);
        updateBody.put("name", "Persistence Test Product Updated");
        mockMvc.perform(put("/api/products/{id}", id)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        Product updated = productMapper.selectById(id);
        assertNotNull(updated);
        assertEquals("Persistence Test Product Updated", updated.getName());

        mockMvc.perform(delete("/api/products/{id}", id)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        assertNull(productMapper.selectById(id));
    }

    private String loginAndGetToken() throws Exception {
        Map<String, Object> loginBody = new LinkedHashMap<>();
        loginBody.put("username", "admin");
        loginBody.put("password", "admin123");

        MvcResult loginResult = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").isNotEmpty())
                .andReturn();

        JsonNode root = objectMapper.readTree(loginResult.getResponse().getContentAsString());
        return root.path("data").path("token").asText();
    }
}