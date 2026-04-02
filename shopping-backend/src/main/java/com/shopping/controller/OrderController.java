package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.dto.OrderCreateRequest;
import com.shopping.interceptor.UserContext;
import com.shopping.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public Result<?> createOrder(@RequestBody OrderCreateRequest request) {
        Long userId = UserContext.get();
        return orderService.createOrder(userId, request);
    }

    @GetMapping
    public Result<?> getOrderList(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        Long userId = UserContext.get();
        return orderService.getOrderList(userId, status, pageNum, pageSize);
    }

    @GetMapping("/{id}")
    public Result<?> getOrderDetail(@PathVariable Long id) {
        Long userId = UserContext.get();
        return orderService.getOrderDetail(userId, id);
    }

    @PutMapping("/{id}/cancel")
    public Result<?> cancelOrder(@PathVariable Long id) {
        Long userId = UserContext.get();
        return orderService.cancelOrder(userId, id);
    }

    @PutMapping("/{id}/pay")
    public Result<?> payOrder(@PathVariable Long id) {
        Long userId = UserContext.get();
        return orderService.payOrder(userId, id);
    }
}
