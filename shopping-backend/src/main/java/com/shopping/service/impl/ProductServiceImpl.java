package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.common.PageResult;
import com.shopping.common.Result;
import com.shopping.dto.ProductQueryRequest;
import com.shopping.entity.Product;
import com.shopping.mapper.ProductMapper;
import com.shopping.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;

    @Override
    public Result<PageResult<Product>> listProducts(ProductQueryRequest request) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1);

        if (StringUtils.hasText(request.getKeyword())) {
            wrapper.like(Product::getName, request.getKeyword())
                   .or()
                   .like(Product::getDescription, request.getKeyword());
        }
        if (request.getCategoryId() != null) {
            wrapper.eq(Product::getCategoryId, request.getCategoryId());
        }
        if (request.getMinPrice() != null) {
            wrapper.ge(Product::getPrice, request.getMinPrice());
        }
        if (request.getMaxPrice() != null) {
            wrapper.le(Product::getPrice, request.getMaxPrice());
        }

        if (StringUtils.hasText(request.getSortBy())) {
            boolean isAsc = !"desc".equalsIgnoreCase(request.getSortOrder());
            switch (request.getSortBy()) {
                case "price" -> wrapper.orderBy(true, isAsc, Product::getPrice);
                case "sales" -> wrapper.orderBy(true, isAsc, Product::getSales);
                case "createTime" -> wrapper.orderBy(true, isAsc, Product::getCreateTime);
                default -> wrapper.orderByDesc(Product::getCreateTime);
            }
        } else {
            wrapper.orderByDesc(Product::getCreateTime);
        }

        Page<Product> page = new Page<>(request.getPageNum(), request.getPageSize());
        Page<Product> result = productMapper.selectPage(page, wrapper);

        return Result.success(PageResult.of(result.getTotal(), result.getRecords(),
                request.getPageNum(), request.getPageSize()));
    }

    @Override
    public Result<Product> getProductById(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error(404, "Product not found");
        }
        return Result.success(product);
    }

    @Override
    public Result<List<Product>> listByCategory(Long categoryId) {
        List<Product> products = productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getCategoryId, categoryId)
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime));
        return Result.success(products);
    }

    @Override
    public Result<PageResult<Product>> searchProducts(String keyword, int pageNum, int pageSize) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .and(w -> w.like(Product::getName, keyword).or().like(Product::getDescription, keyword))
                .orderByDesc(Product::getCreateTime);

        Page<Product> page = new Page<>(pageNum, pageSize);
        Page<Product> result = productMapper.selectPage(page, wrapper);

        return Result.success(PageResult.of(result.getTotal(), result.getRecords(), pageNum, pageSize));
    }

    @Override
    public Result<List<Product>> getHotProducts() {
        List<Product> products = productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getSales)
                .last("LIMIT 10"));
        return Result.success(products);
    }

    @Override
    public Result<List<Product>> getNewProducts() {
        List<Product> products = productMapper.selectList(new LambdaQueryWrapper<Product>()
                .eq(Product::getStatus, 1)
                .orderByDesc(Product::getCreateTime)
                .last("LIMIT 10"));
        return Result.success(products);
    }
}
