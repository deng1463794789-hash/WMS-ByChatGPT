package com.wms.modules.product.controller;

import java.util.List;

import com.wms.common.api.ApiResponse;
import com.wms.modules.product.entity.Category;
import com.wms.modules.product.service.ProductService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CategoryController {

    private final ProductService productService;

    public CategoryController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/categories")
    public ApiResponse<List<Category>> list() {
        return ApiResponse.success(productService.getCategoryList());
    }
}
