package com.wms.modules.product.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wms.modules.product.dto.ProductCreateRequest;
import com.wms.modules.product.dto.ProductUpdateRequest;
import com.wms.modules.product.entity.Product;
import com.wms.modules.product.vo.ProductVO;

public interface ProductService extends IService<Product> {

    IPage<ProductVO> pageProducts(long pageNum, long pageSize, String keyword);

    ProductVO getProduct(Long id);

    Long createProduct(ProductCreateRequest request);

    void updateProduct(Long id, ProductUpdateRequest request);

    void deleteProduct(Long id);
}
