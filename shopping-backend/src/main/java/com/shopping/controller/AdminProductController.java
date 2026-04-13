package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.ProductSaveRequest;
import com.shopping.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @GetMapping
    public Result<?> listProducts(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status) {
        return productService.adminListProducts(pageNum, pageSize, keyword, status);
    }

    @PostMapping
    public Result<?> createProduct(@RequestBody ProductSaveRequest request) {
        return productService.createProduct(request);
    }

    @PutMapping("/{id}")
    public Result<?> updateProduct(@PathVariable Long id, @RequestBody ProductSaveRequest request) {
        return productService.updateProduct(id, request);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @PutMapping("/{id}/status")
    public Result<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        return productService.updateProductStatus(id, status);
    }
}
