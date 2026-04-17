package com.localwarehouse.backend.module.product.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.validation.Valid;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.localwarehouse.backend.common.api.ApiResponse;
import com.localwarehouse.backend.module.product.dto.ProductCreateRequest;
import com.localwarehouse.backend.module.product.dto.ProductUpdateRequest;
import com.localwarehouse.backend.module.product.service.ProductService;
import com.localwarehouse.backend.module.product.vo.ProductVO;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<IPage<ProductVO>> page(@RequestParam(defaultValue = "1") long pageNum,
                                              @RequestParam(defaultValue = "10") long pageSize,
                                              @RequestParam(required = false) String keyword) {
        return ApiResponse.success(productService.pageProducts(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductVO> detail(@PathVariable Long id) {
        return ApiResponse.success(productService.getProduct(id));
    }

    @PostMapping
    public ApiResponse<Map<String, Object>> create(@Valid @RequestBody ProductCreateRequest request) {
        Long id = productService.createProduct(request);
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("id", id);
        return ApiResponse.success("created", result);
    }

    @PutMapping("/{id}")
    public ApiResponse<Void> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        productService.updateProduct(id, request);
        return ApiResponse.success("updated", null);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("deleted", null);
    }
}
