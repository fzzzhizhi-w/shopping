package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.OrderReviewRequest;
import com.shopping.interceptor.UserContext;
import com.shopping.service.OrderReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class OrderReviewController {

    private final OrderReviewService reviewService;

    @PostMapping("/orders/{orderId}")
    public Result<?> createReview(@PathVariable Long orderId, @RequestBody OrderReviewRequest request) {
        return reviewService.createReview(UserContext.get(), orderId, request);
    }

    @GetMapping("/orders/{orderId}")
    public Result<?> getOrderReviews(@PathVariable Long orderId) {
        return reviewService.getReviewsByOrder(UserContext.get(), orderId);
    }

    @GetMapping("/products/{productId}")
    public Result<?> getProductReviews(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return reviewService.getReviewsByProduct(productId, pageNum, pageSize);
    }
}
