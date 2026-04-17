package com.localwarehouse.backend.module.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.localwarehouse.backend.module.product.dto.ProductCreateRequest;
import com.localwarehouse.backend.module.product.dto.ProductUpdateRequest;
import com.localwarehouse.backend.module.product.entity.Product;
import com.localwarehouse.backend.module.product.vo.ProductVO;

public interface ProductService extends IService<Product> {

    IPage<ProductVO> pageProducts(long pageNum, long pageSize, String keyword);

    ProductVO getProduct(Long id);

    Long createProduct(ProductCreateRequest request);

    void updateProduct(Long id, ProductUpdateRequest request);

    void deleteProduct(Long id);
}
