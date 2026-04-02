package com.shopping.service;

import com.shopping.common.Result;

import java.util.List;
import java.util.Map;

public interface CartService {

    Result<?> addToCart(Long userId, Long productId, Integer quantity);

    Result<?> updateCart(Long userId, Long cartId, Integer quantity);

    Result<?> removeFromCart(Long userId, Long cartId);

    Result<List<Map<String, Object>>> getCart(Long userId);

    Result<?> clearCart(Long userId);
}
