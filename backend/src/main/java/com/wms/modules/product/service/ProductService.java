package com.wms.modules.product.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.modules.product.entity.Category;
import com.wms.modules.product.entity.Product;

public interface ProductService extends IService<Product> {

    IPage<Product> pageProducts(long pageNum, long pageSize, String keyword);

    IPage<Product> pageProductsWithFilter(long pageNum, long pageSize, String keyword, String stockStatus);

    Product getProduct(Long id);

    Long createProduct(Product product);

    void updateProduct(Long id, Product product);

    void deleteProduct(Long id);

    void batchDeleteProducts(List<Long> ids);

    void updateProductStock(Long id, Integer stockQuantity, String remark);

    List<Category> getCategoryList();

    Map<String, Object> getDashboardData();
}
