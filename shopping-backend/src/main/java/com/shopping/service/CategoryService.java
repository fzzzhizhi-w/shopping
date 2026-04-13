package com.shopping.service;

import com.shopping.common.Result;
import com.shopping.dto.CategorySaveRequest;
import com.shopping.entity.Category;

import java.util.List;

public interface CategoryService {

    Result<List<Category>> listAllCategories();

    Result<Category> getCategoryById(Long id);

    // Admin operations
    Result<Category> createCategory(CategorySaveRequest request);

    Result<Category> updateCategory(Long id, CategorySaveRequest request);

    Result<?> deleteCategory(Long id);
}
