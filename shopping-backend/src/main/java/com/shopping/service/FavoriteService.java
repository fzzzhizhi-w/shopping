package com.shopping.service;

import com.shopping.common.Result;

public interface FavoriteService {

    Result<?> addFavorite(Long userId, Long productId);

    Result<?> removeFavorite(Long userId, Long productId);

    Result<?> listFavorites(Long userId, int pageNum, int pageSize);

    Result<?> isFavorited(Long userId, Long productId);
}
