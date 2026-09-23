package com.craftmarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.craftmarket.entity.Order;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {

    // 创建订单（摊位分配成功后自动创建）
    boolean createOrder(Long vendorId, Long marketEventId, Long boothId, BigDecimal amount);

    // 根据ID查询订单
    Order getOrderById(Long id);

    // 分页查询订单列表（管理员）
    Page<Order> getOrdersPage(Integer current, Integer size, Long eventId, Long vendorId);

    // 根据摊主ID查询订单
    Page<Order> getOrdersByVendor(Long vendorId, Page<Order> page);

    // 根据活动ID查询订单
    Page<Order> getOrdersByEvent(Long eventId, Page<Order> page);

    // 根据摊主和活动查询订单
    Page<Order> getOrdersByVendorAndEvent(Long vendorId, Long eventId, Page<Order> page);

    // 更新订单状态
    boolean updateOrderStatus(Long orderId, Order.OrderStatus status);

    // 删除订单
    boolean deleteOrder(Long id);

    // 获取某个摊主的总订单金额
    BigDecimal getTotalAmountByVendor(Long vendorId);
}