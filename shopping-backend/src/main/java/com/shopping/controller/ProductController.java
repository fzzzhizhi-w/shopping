package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.ProductQueryRequest;
import com.shopping.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Result<?> listProducts(ProductQueryRequest request) {
        return productService.listProducts(request);
    }

    @GetMapping("/{id}")
    public Result<?> getProductById(@PathVariable Long id) {
        return productService.getProductById(id);
    }

    @GetMapping("/hot")
    public Result<?> getHotProducts() {
        return productService.getHotProducts();
    }

    @GetMapping("/new")
    public Result<?> getNewProducts() {
        return productService.getNewProducts();
    }

    @GetMapping("/search")
    public Result<?> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return productService.searchProducts(keyword, pageNum, pageSize);
    }
}
