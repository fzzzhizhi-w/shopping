package com.shopping.service;

import com.shopping.common.Result;
import com.shopping.entity.Category;

import java.util.List;

public interface CategoryService {

    Result<List<Category>> listAllCategories();

    Result<Category> getCategoryById(Long id);
}
