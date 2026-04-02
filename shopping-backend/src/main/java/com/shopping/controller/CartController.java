package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.interceptor.UserContext;
import com.shopping.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public Result<?> getCart() {
        Long userId = UserContext.get();
        return cartService.getCart(userId);
    }

    @PostMapping("/add")
    public Result<?> addToCart(@RequestBody Map<String, Object> body) {
        Long userId = UserContext.get();
        Long productId = Long.valueOf(body.get("productId").toString());
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        return cartService.addToCart(userId, productId, quantity);
    }

    @PutMapping("/{cartId}")
    public Result<?> updateCart(@PathVariable Long cartId, @RequestBody Map<String, Object> body) {
        Long userId = UserContext.get();
        Integer quantity = Integer.valueOf(body.get("quantity").toString());
        return cartService.updateCart(userId, cartId, quantity);
    }

    @DeleteMapping("/{cartId}")
    public Result<?> removeFromCart(@PathVariable Long cartId) {
        Long userId = UserContext.get();
        return cartService.removeFromCart(userId, cartId);
    }

    @DeleteMapping("/clear")
    public Result<?> clearCart() {
        Long userId = UserContext.get();
        return cartService.clearCart(userId);
    }
}
