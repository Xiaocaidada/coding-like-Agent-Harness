package com.craftmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.craftmarket.entity.Booth;
import com.craftmarket.entity.Order;
import com.craftmarket.entity.Vendor;
import com.craftmarket.enums.OrderStatus;
import com.craftmarket.mapper.BoothMapper;
import com.craftmarket.mapper.OrderMapper;
import com.craftmarket.mapper.VendorMapper;
import com.craftmarket.service.BoothService;
import com.craftmarket.service.OrderService;
import com.craftmarket.service.VendorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    @Autowired
    private VendorMapper vendorMapper;
    
    @Autowired
    private BoothMapper boothMapper;
    
    @Autowired
    private BoothService boothService;

    @Override
    @Transactional
    public boolean createOrder(Long vendorId, Long marketEventId, Long boothId, BigDecimal amount) {
        // 验证摊主是否存在
        Vendor vendor = vendorMapper.selectById(vendorId);
        if (vendor == null) {
            throw new IllegalArgumentException("摊主不存在");
        }
        
        // 验证摊位是否存在且可用
        Booth booth = boothMapper.selectById(boothId);
        if (booth == null) {
            throw new IllegalArgumentException("摊位不存在");
        }
        
        if (!"AVAILABLE".equals(booth.getStatus())) {
            throw new IllegalStateException("该摊位已被分配或禁用");
        }
        
        // 创建订单
        Order order = new Order();
        order.setVendorId(vendorId);
        order.setMarketEventId(marketEventId);
        order.setBoothId(boothId);
        order.setAmount(amount);
        order.setStatus(OrderStatus.PENDING);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        // 保存订单
        boolean result = save(order);
        
        // 如果订单创建成功，更新摊位状态
        if (result) {
            boothService.updateBoothStatus(boothId, "ASSIGNED");
        }
        
        return result;
    }

    @Override
    public Order getOrderById(Long id) {
        return getById(id);
    }

    @Override
    public Page<Order> getOrdersPage(Integer current, Integer size, Long eventId, Long vendorId) {
        Page<Order> page = new Page<>(current, size);
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        
        if (eventId != null) {
            queryWrapper.eq(Order::getMarketEventId, eventId);
        }
        
        if (vendorId != null) {
            queryWrapper.eq(Order::getVendorId, vendorId);
        }
        
        queryWrapper.orderByDesc(Order::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    public Page<Order> getOrdersByVendor(Long vendorId, Page<Order> page) {
        return lambdaQuery()
                .eq(Order::getVendorId, vendorId)
                .orderByDesc(Order::getCreateTime)
                .page(page);
    }

    @Override
    public Page<Order> getOrdersByEvent(Long eventId, Page<Order> page) {
        return lambdaQuery()
                .eq(Order::getMarketEventId, eventId)
                .orderByDesc(Order::getCreateTime)
                .page(page);
    }

    @Override
    public Page<Order> getOrdersByVendorAndEvent(Long vendorId, Long eventId, Page<Order> page) {
        return lambdaQuery()
                .eq(Order::getVendorId, vendorId)
                .eq(Order::getMarketEventId, eventId)
                .orderByDesc(Order::getCreateTime)
                .page(page);
    }

    @Override
    @Transactional
    public boolean updateOrderStatus(Long orderId, OrderStatus status) {
        Order order = getById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        
        return updateById(order);
    }

    @Override
    @Transactional
    public boolean deleteOrder(Long id) {
        Order order = getById(id);
        if (order == null) {
            throw new IllegalArgumentException("订单不存在");
        }
        
        // 如果订单是已支付状态，不能删除
        if (OrderStatus.PAID.equals(order.getStatus())) {
            throw new IllegalStateException("已支付的订单不能删除");
        }
        
        // 删除订单后，将摊位状态恢复为可用
        if (order.getBoothId() != null) {
            boothService.updateBoothStatus(order.getBoothId(), "AVAILABLE");
        }
        
        return removeById(id);
    }

    @Override
    public BigDecimal getTotalAmountByVendor(Long vendorId) {
        LambdaQueryWrapper<Order> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Order::getVendorId, vendorId)
                .eq(Order::getStatus, OrderStatus.PAID);
        
        List<Order> orders = list(queryWrapper);
        
        return orders.stream()
                .map(Order::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}