package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.interceptor.UserContext;
import com.shopping.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommend")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @GetMapping
    public Result<?> getRecommendations(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String budget) {
        Long userId = UserContext.get();
        return recommendService.getAiRecommendations(userId, category, budget);
    }
}
