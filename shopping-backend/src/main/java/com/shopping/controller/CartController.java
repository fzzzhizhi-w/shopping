package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.AddToCartRequest;
import com.shopping.dto.UpdateCartRequest;
import com.shopping.interceptor.UserContext;
import com.shopping.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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
    public Result<?> addToCart(@RequestBody AddToCartRequest request) {
        Long userId = UserContext.get();
        return cartService.addToCart(userId, request.getProductId(), request.getQuantity());
    }

    @PutMapping("/{cartId}")
    public Result<?> updateCart(@PathVariable Long cartId, @RequestBody UpdateCartRequest request) {
        Long userId = UserContext.get();
        return cartService.updateCart(userId, cartId, request.getQuantity());
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
