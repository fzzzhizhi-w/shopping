package com.shopping.service.impl;

import com.shopping.common.Result;
import com.shopping.entity.Category;
import com.shopping.mapper.CategoryMapper;
import com.shopping.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Override
    public Result<List<Category>> listAllCategories() {
        List<Category> categories = categoryMapper.selectList(null);
        return Result.success(categories);
    }

    @Override
    public Result<Category> getCategoryById(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.error(404, "Category not found");
        }
        return Result.success(category);
    }
}
