package com.shopping.service;

import com.shopping.common.Result;
import com.shopping.dto.OrderReviewRequest;

public interface OrderReviewService {

    Result<?> createReview(Long userId, Long orderId, OrderReviewRequest request);

    Result<?> getReviewsByProduct(Long productId, int pageNum, int pageSize);

    Result<?> getReviewsByOrder(Long userId, Long orderId);

    Result<?> adminListReviews(int pageNum, int pageSize, Long productId);
}
