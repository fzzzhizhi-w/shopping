package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public Result<?> listAllCategories() {
        return categoryService.listAllCategories();
    }
}
