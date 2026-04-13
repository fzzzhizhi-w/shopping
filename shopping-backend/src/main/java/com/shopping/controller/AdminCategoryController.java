package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.CategorySaveRequest;
import com.shopping.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<?> listCategories() {
        return categoryService.listAllCategories();
    }

    @PostMapping
    public Result<?> createCategory(@RequestBody CategorySaveRequest request) {
        return categoryService.createCategory(request);
    }

    @PutMapping("/{id}")
    public Result<?> updateCategory(@PathVariable Long id, @RequestBody CategorySaveRequest request) {
        return categoryService.updateCategory(id, request);
    }

    @DeleteMapping("/{id}")
    public Result<?> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}
