package com.shopping.service;

import com.shopping.common.PageResult;
import com.shopping.common.Result;
import com.shopping.dto.OrderCreateRequest;
import com.shopping.entity.Order;

public interface OrderService {

    Result<Order> createOrder(Long userId, OrderCreateRequest request);

    Result<PageResult<?>> getOrderList(Long userId, Integer status, int pageNum, int pageSize);

    Result<?> getOrderDetail(Long userId, Long orderId);

    Result<?> cancelOrder(Long userId, Long orderId);

    Result<?> payOrder(Long userId, Long orderId);
}
