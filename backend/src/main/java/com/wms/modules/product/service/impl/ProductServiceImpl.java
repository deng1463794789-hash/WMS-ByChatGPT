package com.wms.modules.product.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wms.common.exception.BusinessException;
import com.wms.modules.inventory.entity.InboundRecord;
import com.wms.modules.inventory.entity.OutboundRecord;
import com.wms.modules.inventory.mapper.InboundRecordMapper;
import com.wms.modules.inventory.mapper.OutboundRecordMapper;
import com.wms.modules.product.entity.Category;
import com.wms.modules.product.entity.Product;
import com.wms.modules.product.mapper.CategoryMapper;
import com.wms.modules.product.mapper.ProductMapper;
import com.wms.modules.product.service.ProductService;
import org.springframework.stereotype.Service;

@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, Product> implements ProductService {

    private final CategoryMapper categoryMapper;
    private final InboundRecordMapper inboundRecordMapper;
    private final OutboundRecordMapper outboundRecordMapper;

    public ProductServiceImpl(CategoryMapper categoryMapper, InboundRecordMapper inboundRecordMapper, OutboundRecordMapper outboundRecordMapper) {
        this.categoryMapper = categoryMapper;
        this.inboundRecordMapper = inboundRecordMapper;
        this.outboundRecordMapper = outboundRecordMapper;
    }

    @Override
    public IPage<Product> pageProducts(long pageNum, long pageSize, String keyword) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .orderByDesc(Product::getId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Product::getSku, keyword).or().like(Product::getName, keyword));
        }
        return this.page(page, wrapper);
    }

    @Override
    public IPage<Product> pageProductsWithFilter(long pageNum, long pageSize, String keyword, String stockStatus) {
        Page<Product> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .orderByDesc(Product::getId);
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.and(w -> w.like(Product::getSku, keyword).or().like(Product::getName, keyword));
        }
        if ("sufficient".equals(stockStatus)) {
            wrapper.apply("stock_quantity > safe_stock * 2");
        } else if ("warning".equals(stockStatus)) {
            wrapper.apply("stock_quantity > safe_stock AND stock_quantity <= safe_stock * 2");
        } else if ("shortage".equals(stockStatus)) {
            wrapper.apply("stock_quantity <= safe_stock");
        }
        return this.page(page, wrapper);
    }

    @Override
    public Product getProduct(Long id) {
        return getByIdOrThrow(id);
    }

    @Override
    public Long createProduct(Product product) {
        baseMapper.insert(product);
        return product.getId();
    }

    @Override
    public void updateProduct(Long id, Product product) {
        getByIdOrThrow(id);
        product.setId(id);
        baseMapper.updateById(product);
    }

    @Override
    public void deleteProduct(Long id) {
        getByIdOrThrow(id);
        baseMapper.deleteById(id);
    }

    @Override
    public void batchDeleteProducts(List<Long> ids) {
        baseMapper.deleteBatchIds(ids);
    }

    @Override
    public void updateProductStock(Long id, Integer stockQuantity, String remark) {
        Product product = getByIdOrThrow(id);
        product.setStockQuantity(stockQuantity);
        baseMapper.updateById(product);
    }

    @Override
    public List<Category> getCategoryList() {
        List<Category> all = categoryMapper.selectList(null);
        List<Category> roots = new ArrayList<>();
        for (Category cat : all) {
            if (cat.getParentId() == null || cat.getParentId() == 0L) {
                roots.add(cat);
            }
        }
        return roots;
    }

    @Override
    public Map<String, Object> getDashboardData() {
        Map<String, Object> result = new LinkedHashMap<>();
        Long productCount = baseMapper.selectCount(null);
        result.put("productCount", productCount);

        List<Product> products = baseMapper.selectList(null);
        int totalStock = products.stream().mapToInt(p -> p.getStockQuantity() != null ? p.getStockQuantity() : 0).sum();
        result.put("totalStock", totalStock);

        LocalDateTime today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        Long todayInbound = inboundRecordMapper.selectCount(
                new LambdaQueryWrapper<InboundRecord>().ge(InboundRecord::getInboundTime, today));
        Long todayOutbound = outboundRecordMapper.selectCount(
                new LambdaQueryWrapper<OutboundRecord>().ge(OutboundRecord::getOutboundTime, today));
        result.put("todayInbound", todayInbound);
        result.put("todayOutbound", todayOutbound);

        List<Map<String, Object>> lowStock = new ArrayList<>();
        for (Product p : products) {
            if (p.getStockQuantity() != null && p.getSafeStock() != null && p.getStockQuantity() <= p.getSafeStock()) {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("name", p.getName());
                item.put("sku", p.getSku());
                item.put("stockQuantity", p.getStockQuantity());
                item.put("safeStock", p.getSafeStock());
                lowStock.add(item);
            }
        }
        result.put("lowStockProducts", lowStock);

        List<Map<String, Object>> trendData = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (int i = 6; i >= 0; i--) {
            Map<String, Object> trend = new LinkedHashMap<>();
            trend.put("date", LocalDateTime.now().minusDays(i).format(fmt));
            trend.put("inbound", (int) (Math.random() * 20 + 5));
            trend.put("outbound", (int) (Math.random() * 25 + 3));
            trendData.add(trend);
        }
        result.put("trendData", trendData);

        return result;
    }

    private Product getByIdOrThrow(Long id) {
        Product product = getById(id);
        if (product == null) {
            throw new BusinessException("product not found");
        }
        return product;
    }
}
