package com.shopping.service;

import com.shopping.common.PageResult;
import com.shopping.common.Result;
import com.shopping.dto.ProductQueryRequest;
import com.shopping.entity.Product;

import java.util.List;

public interface ProductService {

    Result<PageResult<Product>> listProducts(ProductQueryRequest request);

    Result<Product> getProductById(Long id);

    Result<List<Product>> listByCategory(Long categoryId);

    Result<PageResult<Product>> searchProducts(String keyword, int pageNum, int pageSize);

    Result<List<Product>> getHotProducts();

    Result<List<Product>> getNewProducts();
}
