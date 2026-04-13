package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.service.OrderReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/reviews")
@RequiredArgsConstructor
public class AdminReviewController {

    private final OrderReviewService reviewService;

    @GetMapping
    public Result<?> list(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Long productId) {
        return reviewService.adminListReviews(pageNum, pageSize, productId);
    }
}
