package com.shopping.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.common.PageResult;
import com.shopping.common.Result;
import com.shopping.dto.ProductSaveRequest;
import com.shopping.entity.Product;
import com.shopping.interceptor.UserContext;
import com.shopping.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/merchant/products")
@RequiredArgsConstructor
public class MerchantProductController {

    private final ProductMapper productMapper;

    @GetMapping
    public Result<?> listProducts(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        Long merchantId = UserContext.get();
        LambdaQueryWrapper<Product> wrapper = new LambdaQueryWrapper<Product>()
                .eq(Product::getMerchantId, merchantId);
        if (StringUtils.hasText(keyword)) {
            wrapper.like(Product::getName, keyword);
        }
        if (status != null) {
            wrapper.eq(Product::getStatus, status);
        }
        wrapper.orderByDesc(Product::getCreateTime);
        Page<Product> page = new Page<>(pageNum, pageSize);
        Page<Product> result = productMapper.selectPage(page, wrapper);
        return Result.success(PageResult.of(result.getTotal(), result.getRecords(), pageNum, pageSize));
    }

    @PostMapping
    public Result<?> createProduct(@RequestBody ProductSaveRequest request) {
        Long merchantId = UserContext.get();
        Product product = new Product();
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock() != null ? request.getStock() : 0);
        product.setCategoryId(request.getCategoryId());
        product.setImages(request.getImages());
        product.setMainImage(request.getMainImage());
        product.setStatus(request.getStatus() != null ? request.getStatus() : 1);
        product.setMerchantId(merchantId);
        productMapper.insert(product);
        return Result.success(product);
    }

    @PutMapping("/{id}")
    public Result<?> updateProduct(@PathVariable Long id, @RequestBody ProductSaveRequest request) {
        Long merchantId = UserContext.get();
        Product existing = productMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "Product not found");
        }
        if (!merchantId.equals(existing.getMerchantId())) {
            return Result.error(403, "Forbidden: not your product");
        }
        if (StringUtils.hasText(request.getName())) existing.setName(request.getName());
        if (request.getDescription() != null) existing.setDescription(request.getDescription());
        if (request.getPrice() != null) existing.setPrice(request.getPrice());
        if (request.getStock() != null) existing.setStock(request.getStock());
        if (request.getCategoryId() != null) existing.setCategoryId(request.getCategoryId());
        if (request.getImages() != null) existing.setImages(request.getImages());
        if (request.getMainImage() != null) existing.setMainImage(request.getMainImage());
        if (request.getStatus() != null) existing.setStatus(request.getStatus());
        productMapper.updateById(existing);
        return Result.success(existing);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteProduct(@PathVariable Long id) {
        Long merchantId = UserContext.get();
        Product existing = productMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "Product not found");
        }
        if (!merchantId.equals(existing.getMerchantId())) {
            return Result.error(403, "Forbidden: not your product");
        }
        productMapper.deleteById(id);
        return Result.success("Deleted");
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestBody java.util.Map<String, Integer> body) {
        Long merchantId = UserContext.get();
        Product existing = productMapper.selectById(id);
        if (existing == null) {
            return Result.error(404, "Product not found");
        }
        if (!merchantId.equals(existing.getMerchantId())) {
            return Result.error(403, "Forbidden: not your product");
        }
        existing.setStatus(body.get("status"));
        productMapper.updateById(existing);
        return Result.success("Status updated");
    }
}
