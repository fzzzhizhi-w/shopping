package com.shopping.service.impl;

import com.shopping.common.Result;
import com.shopping.dto.CategorySaveRequest;
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

    @Override
    public Result<Category> createCategory(CategorySaveRequest request) {
        Category category = new Category();
        category.setName(request.getName());
        category.setIcon(request.getIcon());
        category.setSort(request.getSort() != null ? request.getSort() : 0);
        category.setParentId(request.getParentId() != null ? request.getParentId() : 0L);
        categoryMapper.insert(category);
        return Result.success(category);
    }

    @Override
    public Result<Category> updateCategory(Long id, CategorySaveRequest request) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.error(404, "Category not found");
        }
        if (request.getName() != null) category.setName(request.getName());
        if (request.getIcon() != null) category.setIcon(request.getIcon());
        if (request.getSort() != null) category.setSort(request.getSort());
        if (request.getParentId() != null) category.setParentId(request.getParentId());
        categoryMapper.updateById(category);
        return Result.success(category);
    }

    @Override
    public Result<?> deleteCategory(Long id) {
        Category category = categoryMapper.selectById(id);
        if (category == null) {
            return Result.error(404, "Category not found");
        }
        categoryMapper.deleteById(id);
        return Result.success("Category deleted");
    }
}
