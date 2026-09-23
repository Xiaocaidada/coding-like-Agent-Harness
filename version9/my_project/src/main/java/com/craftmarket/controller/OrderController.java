package com.craftmarket.controller;

import com.craftmarket.entity.Order;
import com.craftmarket.service.OrderService;
import com.craftmarket.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public Result<?> createOrder(@RequestParam Long vendorId, 
                               @RequestParam Long marketEventId, 
                               @RequestParam Long boothId, 
                               @RequestParam BigDecimal amount) {
        return Result.success(orderService.createOrder(vendorId, marketEventId, boothId, amount));
    }

    @GetMapping("/{id}")
    public Result<?> getOrderById(@PathVariable Long id) {
        return Result.success(orderService.getOrderById(id));
    }

    @PutMapping("/{id}")
    public Result<?> updateOrder(@PathVariable Long id, @RequestBody Order order) {
        order.setId(id);
        return Result.success(orderService.updateOrderStatus(id, order.getStatus()));
    }

    @GetMapping
    public Result<?> getAllOrders(@RequestParam(defaultValue = "1") int page,
                                @RequestParam(defaultValue = "10") int size,
                                @RequestParam(required = false) Long eventId,
                                @RequestParam(required = false) Long vendorId) {
        Page<Order> orders = orderService.getOrdersPage(page, size, eventId, vendorId);
        return Result.success(orders);
    }

    @GetMapping("/vendor/{vendorId}")
    public Result<?> getOrdersByVendor(@PathVariable Long vendorId,
                                     @RequestParam(defaultValue = "1") int page,
                                     @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderService.getOrdersByVendor(vendorId, new Page<>(page, size));
        return Result.success(orders);
    }

    @GetMapping("/customer/{customerId}")
    public Result<?> getOrdersByCustomer(@PathVariable Long customerId,
                                       @RequestParam(defaultValue = "1") int page,
                                       @RequestParam(defaultValue = "10") int size) {
        // 暂时重定向到摊主订单查询
        return getOrdersByVendor(customerId, page, size);
    }

    @GetMapping("/market-event/{marketEventId}")
    public Result<?> getOrdersByMarketEvent(@PathVariable Long marketEventId,
                                          @RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Page<Order> orders = orderService.getOrdersByEvent(marketEventId, new Page<>(page, size));
        return Result.success(orders);
    }

    @PutMapping("/{id}/confirm")
    public Result<?> confirmOrder(@PathVariable Long id) {
        return Result.success(orderService.updateOrderStatus(id, Order.OrderStatus.CONFIRMED));
    }

    @PutMapping("/{id}/cancel")
    public Result<?> cancelOrder(@PathVariable Long id) {
        return Result.success(orderService.updateOrderStatus(id, Order.OrderStatus.CANCELLED));
    }

    @PutMapping("/{id}/complete")
    public Result<?> completeOrder(@PathVariable Long id) {
        return Result.success(orderService.updateOrderStatus(id, Order.OrderStatus.COMPLETED));
    }

    @GetMapping("/stats/{vendorId}")
    public Result<?> getOrderStatistics(@PathVariable Long vendorId) {
        return Result.success(orderService.getTotalAmountByVendor(vendorId));
    }
}