package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.common.PageResult;
import com.shopping.common.Result;
import com.shopping.entity.Product;
import com.shopping.entity.ProductFavorite;
import com.shopping.mapper.ProductFavoriteMapper;
import com.shopping.mapper.ProductMapper;
import com.shopping.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final ProductFavoriteMapper favoriteMapper;
    private final ProductMapper productMapper;

    @Override
    public Result<?> addFavorite(Long userId, Long productId) {
        ProductFavorite existing = favoriteMapper.selectOne(new LambdaQueryWrapper<ProductFavorite>()
                .eq(ProductFavorite::getUserId, userId)
                .eq(ProductFavorite::getProductId, productId));
        if (existing != null) {
            return Result.success("Already favorited");
        }
        ProductFavorite fav = new ProductFavorite();
        fav.setUserId(userId);
        fav.setProductId(productId);
        favoriteMapper.insert(fav);
        return Result.success("Added to favorites");
    }

    @Override
    public Result<?> removeFavorite(Long userId, Long productId) {
        favoriteMapper.delete(new LambdaQueryWrapper<ProductFavorite>()
                .eq(ProductFavorite::getUserId, userId)
                .eq(ProductFavorite::getProductId, productId));
        return Result.success("Removed from favorites");
    }

    @Override
    public Result<?> listFavorites(Long userId, int pageNum, int pageSize) {
        Page<ProductFavorite> page = new Page<>(pageNum, pageSize);
        Page<ProductFavorite> result = favoriteMapper.selectPage(page, new LambdaQueryWrapper<ProductFavorite>()
                .eq(ProductFavorite::getUserId, userId)
                .orderByDesc(ProductFavorite::getCreateTime));

        List<Map<String, Object>> list = new ArrayList<>();
        for (ProductFavorite fav : result.getRecords()) {
            Product product = productMapper.selectById(fav.getProductId());
            Map<String, Object> item = new HashMap<>();
            item.put("favoriteId", fav.getId());
            item.put("createTime", fav.getCreateTime());
            item.put("product", product);
            list.add(item);
        }
        return Result.success(PageResult.of(result.getTotal(), list, pageNum, pageSize));
    }

    @Override
    public Result<?> isFavorited(Long userId, Long productId) {
        long count = favoriteMapper.selectCount(new LambdaQueryWrapper<ProductFavorite>()
                .eq(ProductFavorite::getUserId, userId)
                .eq(ProductFavorite::getProductId, productId));
        return Result.success(count > 0);
    }
}
