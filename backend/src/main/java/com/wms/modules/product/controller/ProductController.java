package com.wms.modules.product.controller;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.wms.common.annotation.AuditLog;
import com.wms.common.api.ApiResponse;
import com.wms.modules.product.entity.Product;
import com.wms.modules.product.service.ProductService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public ApiResponse<IPage<Product>> page(@RequestParam(defaultValue = "1") long pageNum,
                                            @RequestParam(defaultValue = "10") long pageSize,
                                            @RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) String stockStatus) {
        if (stockStatus != null && !stockStatus.isEmpty()) {
            return ApiResponse.success(productService.pageProductsWithFilter(pageNum, pageSize, keyword, stockStatus));
        }
        return ApiResponse.success(productService.pageProducts(pageNum, pageSize, keyword));
    }

    @GetMapping("/{id}")
    public ApiResponse<Product> detail(@PathVariable Long id) {
        return ApiResponse.success(productService.getProduct(id));
    }

    @PostMapping
    @AuditLog(value = "创建商品", module = "商品管理")
    public ApiResponse<Long> create(@RequestBody Product product) {
        return ApiResponse.success(productService.createProduct(product));
    }

    @PutMapping("/{id}")
    @AuditLog(value = "修改商品", module = "商品管理")
    public ApiResponse<Void> update(@PathVariable Long id, @RequestBody Product product) {
        productService.updateProduct(id, product);
        return ApiResponse.success("updated", null);
    }

    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN")
    @AuditLog(value = "删除商品", module = "商品管理")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ApiResponse.success("deleted", null);
    }

    @PostMapping("/batch-delete")
    @Secured("ROLE_ADMIN")
    @AuditLog(value = "批量删除商品", module = "商品管理")
    public ApiResponse<Void> batchDelete(@RequestBody Map<String, List<Long>> body) {
        productService.batchDeleteProducts(body.get("ids"));
        return ApiResponse.success("deleted", null);
    }

    @PutMapping("/{id}/stock")
    @AuditLog(value = "调整库存", module = "商品管理")
    public ApiResponse<Void> updateStock(@PathVariable Long id, @RequestBody Map<String, Object> body) {
        Integer stockQuantity = body.get("stockQuantity") != null ? ((Number) body.get("stockQuantity")).intValue() : null;
        String remark = (String) body.get("remark");
        productService.updateProductStock(id, stockQuantity, remark);
        return ApiResponse.success("updated", null);
    }

    @GetMapping("/export")
    public ApiResponse<Void> exportData() {
        return ApiResponse.success(null);
    }
}
