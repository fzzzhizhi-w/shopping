package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.common.PageResult;
import com.shopping.common.Result;
import com.shopping.dto.ProductQueryRequest;
import com.shopping.dto.ProductSaveRequest;
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

    @Override
    public Result<PageResult<Product>> adminListProducts(int pageNum, int pageSize, String keyword, Integer status) {
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .orderByDesc(Product::getCreateTime);

        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }

        Page<Product> page = new Page<>(pageNum, pageSize);
        Page<Product> result = productMapper.selectPage(page, wrapper);

        return Result.success(PageResult.of(result.getTotal(), result.getRecords(), pageNum, pageSize));
    }

    @Override
    public Result<Product> createProduct(ProductSaveRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock() != null ? request.getStock() : 0);
        product.setCategoryId(request.getCategoryId());
        product.setImages(request.getImages());
        product.setMainImage(request.getMainImage());
        product.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        product.setSales(0);
        productMapper.insert(product);
        return Result.success(product);
    }

    @Override
    public Result<Product> updateProduct(Long id, ProductSaveRequest request) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error(404, "Product not found");
        }
        if (request.getName() != null) product.setName(request.getName());
        if (request.getDescription() != null) product.setDescription(request.getDescription());
        if (request.getPrice() != null) product.setPrice(request.getPrice());
        if (request.getStock() != null) product.setStock(request.getStock());
        if (request.getCategoryId() != null) product.setCategoryId(request.getCategoryId());
        if (request.getImages() != null) product.setImages(request.getImages());
        if (request.getMainImage() != null) product.setMainImage(request.getMainImage());
        if (request.getStatus() != null) product.setStatus(request.getStatus());
        productMapper.updateById(product);
        return Result.success(product);
    }

    @Override
    public Result<?> deleteProduct(Long id) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error(404, "Product not found");
        }
        productMapper.deleteById(id);
        return Result.success("Product deleted");
    }

    @Override
    public Result<?> updateProductStatus(Long id, Integer status) {
        Product product = productMapper.selectById(id);
        if (product == null) {
            return Result.error(404, "Product not found");
        }
        product.setStatus(status);
        productMapper.updateById(product);
        return Result.success("Status updated");
    }
}
