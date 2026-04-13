package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.interceptor.UserContext;
import com.shopping.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return favoriteService.listFavorites(UserContext.get(), pageNum, pageSize);
    }

    @PostMapping("/{productId}")
    public Result<?> add(@PathVariable Long productId) {
        return favoriteService.addFavorite(UserContext.get(), productId);
    }

    @DeleteMapping("/{productId}")
    public Result<?> remove(@PathVariable Long productId) {
        return favoriteService.removeFavorite(UserContext.get(), productId);
    }

    @GetMapping("/{productId}/status")
    public Result<?> status(@PathVariable Long productId) {
        return favoriteService.isFavorited(UserContext.get(), productId);
    }
}
