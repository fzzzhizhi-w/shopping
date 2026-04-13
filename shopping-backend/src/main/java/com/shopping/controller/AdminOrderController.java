package com.shopping.controller;

import com.shopping.common.Result;
import com.shopping.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public Result<?> listOrders(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize) {
        return orderService.adminListOrders(status, pageNum, pageSize);
    }

    @GetMapping("/{id}")
    public Result<?> getOrderDetail(@PathVariable Long id) {
        return orderService.adminGetOrderDetail(id);
    }

    @PutMapping("/{id}/ship")
    public Result<?> shipOrder(@PathVariable Long id) {
        return orderService.shipOrder(id);
    }

    @PutMapping("/{id}/deliver")
    public Result<?> deliverOrder(@PathVariable Long id) {
        return orderService.deliverOrder(id);
    }
}
