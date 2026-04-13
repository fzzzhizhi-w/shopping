package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.common.PageResult;
import com.shopping.common.Result;
import com.shopping.dto.OrderReviewRequest;
import com.shopping.entity.Order;
import com.shopping.entity.OrderItem;
import com.shopping.entity.OrderReview;
import com.shopping.entity.Product;
import com.shopping.mapper.OrderItemMapper;
import com.shopping.mapper.OrderMapper;
import com.shopping.mapper.OrderReviewMapper;
import com.shopping.mapper.ProductMapper;
import com.shopping.service.OrderReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderReviewServiceImpl implements OrderReviewService {

    private final OrderReviewMapper reviewMapper;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    @Override
    public Result<?> createReview(Long userId, Long orderId, OrderReviewRequest request) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));
        if (order == null) {
            return Result.error(404, "Order not found");
        }
        if (order.getStatus() != 3) {
            return Result.error(400, "Can only review delivered orders");
        }
        long exists = reviewMapper.selectCount(new LambdaQueryWrapper<OrderReview>()
                .eq(OrderReview::getOrderItemId, request.getOrderItemId()));
        if (exists > 0) {
            return Result.error(400, "Already reviewed this item");
        }
        OrderReview review = new OrderReview();
        review.setUserId(userId);
        review.setOrderId(orderId);
        review.setOrderItemId(request.getOrderItemId());
        review.setProductId(request.getProductId());
        review.setRating(request.getRating() != null ? request.getRating() : 5);
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        reviewMapper.insert(review);
        return Result.success(review);
    }

    @Override
    public Result<?> getReviewsByProduct(Long productId, int pageNum, int pageSize) {
        Page<OrderReview> page = new Page<>(pageNum, pageSize);
        Page<OrderReview> result = reviewMapper.selectPage(page, new LambdaQueryWrapper<OrderReview>()
                .eq(OrderReview::getProductId, productId)
                .orderByDesc(OrderReview::getCreateTime));
        return Result.success(PageResult.of(result.getTotal(), result.getRecords(), pageNum, pageSize));
    }

    @Override
    public Result<?> getReviewsByOrder(Long userId, Long orderId) {
        List<OrderReview> reviews = reviewMapper.selectList(new LambdaQueryWrapper<OrderReview>()
                .eq(OrderReview::getOrderId, orderId)
                .eq(OrderReview::getUserId, userId));
        // Include already-reviewed item IDs for UI
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        List<Long> reviewedItemIds = reviews.stream().map(OrderReview::getOrderItemId).toList();
        List<Map<String, Object>> result = new ArrayList<>();
        for (OrderItem item : items) {
            Map<String, Object> m = new HashMap<>();
            m.put("item", item);
            m.put("reviewed", reviewedItemIds.contains(item.getId()));
            reviews.stream().filter(r -> r.getOrderItemId().equals(item.getId())).findFirst()
                    .ifPresent(r -> m.put("review", r));
            result.add(m);
        }
        return Result.success(result);
    }

    @Override
    public Result<?> adminListReviews(int pageNum, int pageSize, Long productId) {
        LambdaQueryWrapper<OrderReview> wrapper = new LambdaQueryWrapper<OrderReview>()
                .orderByDesc(OrderReview::getCreateTime);
        if (productId != null) {
            wrapper.eq(OrderReview::getProductId, productId);
        }
        Page<OrderReview> page = new Page<>(pageNum, pageSize);
        Page<OrderReview> result = reviewMapper.selectPage(page, wrapper);
        List<Map<String, Object>> list = new ArrayList<>();
        for (OrderReview review : result.getRecords()) {
            Map<String, Object> m = new HashMap<>();
            m.put("review", review);
            Product product = productMapper.selectById(review.getProductId());
            m.put("productName", product != null ? product.getName() : "");
            list.add(m);
        }
        return Result.success(PageResult.of(result.getTotal(), list, pageNum, pageSize));
    }
}
