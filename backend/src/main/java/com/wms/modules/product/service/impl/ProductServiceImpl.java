package com.wms.modules.product.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.exception.BusinessException;
import com.wms.modules.product.dto.ProductCreateRequest;
import com.wms.modules.product.dto.ProductUpdateRequest;
import com.wms.modules.product.entity.Product;
import com.wms.modules.product.mapper.ProductMapper;
import com.wms.modules.product.service.ProductService;
import com.wms.modules.product.vo.ProductVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    @Override
    public IPage<ProductVO> pageProducts(long pageNum, long pageSize, String keyword) {
        Page<Product> page = new Page<Product>(pageNum, pageSize);
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<Product>()
                .orderByDesc(Product::getId);

        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper.like(Product::getSku, keyword)
                    .or()
                    .like(Product::getName, keyword));
        }

        Page<Product> productPage = this.page(page, queryWrapper);
        List<ProductVO> records = new ArrayList<ProductVO>();
        for (Product product : productPage.getRecords()) {
            records.add(toVO(product));
        }

        Page<ProductVO> result = new Page<ProductVO>(pageNum, pageSize);
        result.setTotal(productPage.getTotal());
        result.setRecords(records);
        return result;
    }

    @Override
    public ProductVO getProduct(Long id) {
        Product product = getByIdOrThrow(id);
        return toVO(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createProduct(ProductCreateRequest request) {
        checkSkuUnique(request.getSku(), null);
        Product product = new Product();
        BeanUtils.copyProperties(request, product);
        save(product);
        return product.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateProduct(Long id, ProductUpdateRequest request) {
        Product product = getByIdOrThrow(id);
        checkSkuUnique(request.getSku(), id);
        BeanUtils.copyProperties(request, product);
        updateById(product);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteProduct(Long id) {
        Product product = getByIdOrThrow(id);
        removeById(product.getId());
    }

    private Product getByIdOrThrow(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("product not found");
        }
        return product;
    }

    private void checkSkuUnique(String sku, Long excludeId) {
        LambdaQueryWrapper<Product> queryWrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getSku, sku);
        if (excludeId != null) {
            queryWrapper.ne(Product::getId, excludeId);
        }
        Long count = baseMapper.selectCount(queryWrapper);
        if (count != null && count > 0) {
            throw new BusinessException("sku already exists");
        }
    }

    private ProductVO toVO(Product product) {
        ProductVO vo = new ProductVO();
        BeanUtils.copyProperties(product, vo);
        return vo;
    }
}
