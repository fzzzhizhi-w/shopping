package com.shopping.service;

import com.shopping.common.PageResult;
import com.shopping.common.Result;
import com.shopping.dto.ProductQueryRequest;
import com.shopping.dto.ProductSaveRequest;
import com.shopping.entity.Product;

import java.util.List;

public interface ProductService {

    Result<PageResult<Product>> listProducts(ProductQueryRequest request);

    Result<Product> getProductById(Long id);

    Result<List<Product>> listByCategory(Long categoryId);

    Result<PageResult<Product>> searchProducts(String keyword, int pageNum, int pageSize);

    Result<List<Product>> getHotProducts();

    Result<List<Product>> getNewProducts();

    // Admin operations
    Result<PageResult<Product>> adminListProducts(int pageNum, int pageSize, String keyword, Integer status);

    Result<Product> createProduct(ProductSaveRequest request);

    Result<Product> updateProduct(Long id, ProductSaveRequest request);

    Result<?> deleteProduct(Long id);

    Result<?> updateProductStatus(Long id, Integer status);
}
