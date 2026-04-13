package com.shopping.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.common.PageResult;
import com.shopping.common.Result;
import com.shopping.dto.OrderCreateRequest;
import com.shopping.entity.Cart;
import com.shopping.entity.Order;
import com.shopping.entity.OrderItem;
import com.shopping.entity.Product;
import com.shopping.mapper.CartMapper;
import com.shopping.mapper.OrderItemMapper;
import com.shopping.mapper.OrderMapper;
import com.shopping.mapper.ProductMapper;
import com.shopping.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final CartMapper cartMapper;
    private final ProductMapper productMapper;

    @Override
    @Transactional
    public Result<Order> createOrder(Long userId, OrderCreateRequest request) {
        if (request.getCartIds() == null || request.getCartIds().isEmpty()) {
            return Result.error(400, "No cart items selected");
        }

        List<Cart> cartItems = cartMapper.selectBatchIds(request.getCartIds());
        if (cartItems.isEmpty()) {
            return Result.error(400, "Cart items not found");
        }

        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Cart cart : cartItems) {
            if (!cart.getUserId().equals(userId)) {
                return Result.error(403, "Unauthorized cart item");
            }

            Product product = productMapper.selectById(cart.getProductId());
            if (product == null || product.getStatus() != 1) {
                return Result.error(400, "Product not available: " + cart.getProductId());
            }
            if (product.getStock() < cart.getQuantity()) {
                return Result.error(400, "Insufficient stock for: " + product.getName());
            }

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity()));
            totalAmount = totalAmount.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(product.getId());
            orderItem.setProductName(product.getName());
            orderItem.setProductImage(product.getMainImage());
            orderItem.setPrice(product.getPrice());
            orderItem.setQuantity(cart.getQuantity());
            orderItems.add(orderItem);

            // Deduct stock and increase sales
            product.setStock(product.getStock() - cart.getQuantity());
            product.setSales(product.getSales() + cart.getQuantity());
            productMapper.updateById(product);
        }

        Order order = new Order();
        order.setOrderNo(UUID.randomUUID().toString().replace("-", "").toUpperCase());
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setStatus(0);
        order.setAddress(request.getAddress());
        order.setReceiverName(request.getReceiverName());
        order.setReceiverPhone(request.getReceiverPhone());
        orderMapper.insert(order);

        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderItemMapper.insert(item);
        }

        // Remove purchased cart items
        for (Long cartId : request.getCartIds()) {
            cartMapper.deleteById(cartId);
        }

        return Result.success(order);
    }

    @Override
    public Result<PageResult<?>> getOrderList(Long userId, Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getCreateTime);

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> result = orderMapper.selectPage(page, wrapper);

        List<Map<String, Object>> orderList = new ArrayList<>();
        for (Order order : result.getRecords()) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("order", order);
            List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            orderMap.put("items", items);
            orderList.add(orderMap);
        }

        return Result.success(PageResult.of(result.getTotal(), orderList, pageNum, pageSize));
    }

    @Override
    public Result<?> getOrderDetail(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));

        if (order == null) {
            return Result.error(404, "Order not found");
        }

        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));

        Map<String, Object> detail = new HashMap<>();
        detail.put("order", order);
        detail.put("items", items);

        return Result.success(detail);
    }

    @Override
    @Transactional
    public Result<?> cancelOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));

        if (order == null) {
            return Result.error(404, "Order not found");
        }
        if (order.getStatus() != 0) {
            return Result.error(400, "Only pending orders can be cancelled");
        }

        order.setStatus(4);
        orderMapper.updateById(order);

        // Restore stock
        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));
        for (OrderItem item : items) {
            Product product = productMapper.selectById(item.getProductId());
            if (product != null) {
                product.setStock(product.getStock() + item.getQuantity());
                product.setSales(Math.max(0, product.getSales() - item.getQuantity()));
                productMapper.updateById(product);
            }
        }

        return Result.success("Order cancelled");
    }

    @Override
    public Result<?> payOrder(Long userId, Long orderId) {
        Order order = orderMapper.selectOne(new LambdaQueryWrapper<Order>()
                .eq(Order::getId, orderId)
                .eq(Order::getUserId, userId));

        if (order == null) {
            return Result.error(404, "Order not found");
        }
        if (order.getStatus() != 0) {
            return Result.error(400, "Order is not in pending payment status");
        }

        order.setStatus(1);
        orderMapper.updateById(order);

        return Result.success("Payment successful");
    }

    @Override
    public Result<PageResult<?>> adminListOrders(Integer status, int pageNum, int pageSize) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<Order>()
                .orderByDesc(Order::getCreateTime);

        if (status != null) {
            wrapper.eq(Order::getStatus, status);
        }

        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> result = orderMapper.selectPage(page, wrapper);

        List<Map<String, Object>> orderList = new ArrayList<>();
        for (Order order : result.getRecords()) {
            Map<String, Object> orderMap = new HashMap<>();
            orderMap.put("order", order);
            List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                    .eq(OrderItem::getOrderId, order.getId()));
            orderMap.put("items", items);
            orderList.add(orderMap);
        }

        return Result.success(PageResult.of(result.getTotal(), orderList, pageNum, pageSize));
    }

    @Override
    public Result<?> adminGetOrderDetail(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error(404, "Order not found");
        }

        List<OrderItem> items = orderItemMapper.selectList(new LambdaQueryWrapper<OrderItem>()
                .eq(OrderItem::getOrderId, orderId));

        Map<String, Object> detail = new HashMap<>();
        detail.put("order", order);
        detail.put("items", items);

        return Result.success(detail);
    }

    @Override
    public Result<?> shipOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error(404, "Order not found");
        }
        if (order.getStatus() != 1) {
            return Result.error(400, "Only paid orders can be shipped");
        }
        order.setStatus(2);
        orderMapper.updateById(order);
        return Result.success("Order shipped");
    }

    @Override
    public Result<?> deliverOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            return Result.error(404, "Order not found");
        }
        if (order.getStatus() != 2) {
            return Result.error(400, "Only shipped orders can be marked as delivered");
        }
        order.setStatus(3);
        orderMapper.updateById(order);
        return Result.success("Order delivered");
    }
}
