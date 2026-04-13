package com.shopping.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.common.PageResult;
import com.shopping.common.Result;
import com.shopping.entity.Order;
import com.shopping.entity.OrderItem;
import com.shopping.entity.Product;
import com.shopping.interceptor.UserContext;
import com.shopping.mapper.OrderItemMapper;
import com.shopping.mapper.OrderMapper;
import com.shopping.mapper.ProductMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/merchant/orders")
@RequiredArgsConstructor
public class MerchantOrderController {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ProductMapper productMapper;

    @GetMapping
    public Result<?> listOrders(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer status) {
        Long merchantId = UserContext.get();

        // Find products belonging to this merchant
        List<Product> merchantProducts = productMapper.selectList(
                new LambdaQueryWrapper<Product>().eq(Product::getMerchantId, merchantId));
        if (merchantProducts.isEmpty()) {
            return Result.success(PageResult.of(0L, List.of(), pageNum, pageSize));
        }

        List<Long> productIds = merchantProducts.stream().map(Product::getId).collect(Collectors.toList());

        // Find order items that reference merchant's products
        List<OrderItem> orderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().in(OrderItem::getProductId, productIds));
        if (orderItems.isEmpty()) {
            return Result.success(PageResult.of(0L, List.of(), pageNum, pageSize));
        }

        List<Long> orderIds = orderItems.stream().map(OrderItem::getOrderId).distinct().collect(Collectors.toList());

        // Query orders with pagination
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .in(Order::getId, orderIds)
                .orderByDesc(Order::getCreateTime);
        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> result = orderMapper.selectPage(page, wrapper);

        // Build response with order items
        List<Map<String, Object>> list = new ArrayList<>();
        for (Order order : result.getRecords()) {
            List<OrderItem> items = orderItems.stream()
                    .filter(oi -> oi.getOrderId().equals(order.getId()) && productIds.contains(oi.getProductId()))
                    .collect(Collectors.toList());
            Map<String, Object> entry = new HashMap<>();
            entry.put("order", order);
            entry.put("items", items);
            list.add(entry);
        }

        return Result.success(PageResult.of(result.getTotal(), list, pageNum, pageSize));
    }

    @PutMapping("/{id}/ship")
    public Result<?> shipOrder(@PathVariable Long id) {
        Long merchantId = UserContext.get();

        Order order = orderMapper.selectById(id);
        if (order == null) {
            return Result.error(404, "Order not found");
        }
        if (order.getStatus() != 1) {
            return Result.error(400, "Order cannot be shipped (not in paid status)");
        }

        // Verify merchant has products in this order
        List<Product> merchantProducts = productMapper.selectList(
                new LambdaQueryWrapper<Product>().eq(Product::getMerchantId, merchantId));
        List<Long> productIds = merchantProducts.stream().map(Product::getId).collect(Collectors.toList());

        if (productIds.isEmpty()) {
            return Result.error(403, "No products found for this merchant");
        }

        List<OrderItem> items = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getOrderId, id)
                        .in(OrderItem::getProductId, productIds));

        if (items.isEmpty()) {
            return Result.error(403, "Forbidden: this order does not contain your products");
        }

        order.setStatus(2);
        orderMapper.updateById(order);
        return Result.success("Order shipped");
    }
}
