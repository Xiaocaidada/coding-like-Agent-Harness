package com.craftmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.craftmarket.entity.Booth;
import com.craftmarket.entity.Order;
import com.craftmarket.entity.Vendor;
import com.craftmarket.enums.OrderStatus;
import com.craftmarket.mapper.BoothMapper;
import com.craftmarket.mapper.OrderMapper;
import com.craftmarket.mapper.VendorMapper;
import com.craftmarket.service.BoothService;
import com.craftmarket.service.VendorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private VendorService vendorService;

    @Mock
    private BoothService boothService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Vendor testVendor;
    private Booth testBooth;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        // 设置测试摊主
        testVendor = new Vendor();
        testVendor.setId(1L);
        testVendor.setName("测试摊主");

        // 设置测试摊位
        testBooth = new Booth();
        testBooth.setId(1L);
        testBooth.setName("A区001号摊位");
        testBooth.setPrice(new BigDecimal("500.00"));

        // 设置测试订单
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setVendorId(1L);
        testOrder.setMarketEventId(1L);
        testOrder.setBoothId(1L);
        testOrder.setAmount(new BigDecimal("500.00"));
        testOrder.setStatus(OrderStatus.PENDING);
        testOrder.setCreateTime(LocalDateTime.now());
    }

    @Test
    void createOrder_Success() {
        // Arrange
        when(vendorService.getVendorById(anyLong())).thenReturn(testVendor);
        when(boothService.getBoothById(anyLong())).thenReturn(testBooth);
        when(orderMapper.insert(any(Order.class))).thenReturn(1);

        // Act
        boolean result = orderService.createOrder(1L, 1L, 1L, new BigDecimal("500.00"));

        // Assert
        assertTrue(result);
        verify(orderMapper).insert(any(Order.class));
    }

    @Test
    void createOrder_VendorNotFound() {
        // Arrange
        when(vendorService.getVendorById(anyLong())).thenReturn(null);

        // Act
        boolean result = orderService.createOrder(1L, 1L, 1L, new BigDecimal("500.00"));

        // Assert
        assertFalse(result);
        verify(orderMapper, never()).insert(any(Order.class));
    }

    @Test
    void createOrder_BoothNotFound() {
        // Arrange
        when(vendorService.getVendorById(anyLong())).thenReturn(testVendor);
        when(boothService.getBoothById(anyLong())).thenReturn(null);

        // Act
        boolean result = orderService.createOrder(1L, 1L, 1L, new BigDecimal("500.00"));

        // Assert
        assertFalse(result);
        verify(orderMapper, never()).insert(any(Order.class));
    }

    @Test
    void getOrderById_Success() {
        // Arrange
        when(orderMapper.selectById(anyLong())).thenReturn(testOrder);

        // Act
        Order result = orderService.getOrderById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(new BigDecimal("500.00"), result.getAmount());
        verify(orderMapper).selectById(1L);
    }

    @Test
    void getOrdersPage_Success() {
        // Arrange
        Page<Order> page = new Page<>(1, 10);
        List<Order> orders = Arrays.asList(testOrder);
        page.setRecords(orders);
        
        when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(page);

        // Act
        Page<Order> result = orderService.getOrdersPage(1, 10, 1L, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(new BigDecimal("500.00"), result.getRecords().get(0).getAmount());
        verify(orderMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void getOrdersByVendor_Success() {
        // Arrange
        Page<Order> page = new Page<>(1, 10);
        List<Order> orders = Arrays.asList(testOrder);
        page.setRecords(orders);
        
        when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(page);

        // Act
        Page<Order> result = orderService.getOrdersByVendor(1L, page);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(new BigDecimal("500.00"), result.getRecords().get(0).getAmount());
        verify(orderMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void getOrdersByEvent_Success() {
        // Arrange
        Page<Order> page = new Page<>(1, 10);
        List<Order> orders = Arrays.asList(testOrder);
        page.setRecords(orders);
        
        when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(page);

        // Act
        Page<Order> result = orderService.getOrdersByEvent(1L, page);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(new BigDecimal("500.00"), result.getRecords().get(0).getAmount());
        verify(orderMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void getOrdersByVendorAndEvent_Success() {
        // Arrange
        Page<Order> page = new Page<>(1, 10);
        List<Order> orders = Arrays.asList(testOrder);
        page.setRecords(orders);
        
        when(orderMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(page);

        // Act
        Page<Order> result = orderService.getOrdersByVendorAndEvent(1L, 1L, page);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals(new BigDecimal("500.00"), result.getRecords().get(0).getAmount());
        verify(orderMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void updateOrderStatus_Success() {
        // Arrange
        when(orderMapper.updateById(any(Order.class))).thenReturn(1);

        // Act
        boolean result = orderService.updateOrderStatus(1L, OrderStatus.CONFIRMED);

        // Assert
        assertTrue(result);
        verify(orderMapper).updateById(any(Order.class));
    }

    @Test
    void deleteOrder_Success() {
        // Arrange
        when(orderMapper.deleteById(anyLong())).thenReturn(1);

        // Act
        boolean result = orderService.deleteOrder(1L);

        // Assert
        assertTrue(result);
        verify(orderMapper).deleteById(1L);
    }

    @Test
    void getTotalAmountByVendor_Success() {
        // Arrange
        BigDecimal expectedAmount = new BigDecimal("1500.00");
        when(orderMapper.selectTotalAmountByVendor(anyLong())).thenReturn(expectedAmount);

        // Act
        BigDecimal result = orderService.getTotalAmountByVendor(1L);

        // Assert
        assertNotNull(result);
        assertEquals(expectedAmount, result);
        verify(orderMapper).selectTotalAmountByVendor(1L);
    }

    @Test
    void getTotalAmountByVendor_NoOrders() {
        // Arrange
        when(orderMapper.selectTotalAmountByVendor(anyLong())).thenReturn(BigDecimal.ZERO);

        // Act
        BigDecimal result = orderService.getTotalAmountByVendor(1L);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result);
        verify(orderMapper).selectTotalAmountByVendor(1L);
    }
}