package com.craftmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.craftmarket.entity.MarketEvent;
import com.craftmarket.entity.Booth;
import com.craftmarket.enums.MarketEventStatus;
import com.craftmarket.mapper.MarketEventMapper;
import com.craftmarket.service.BoothService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarketEventServiceImplTest {

    @Mock
    private MarketEventMapper marketEventMapper;

    @Mock
    private BoothService boothService;

    @InjectMocks
    private MarketEventServiceImpl marketEventService;

    private MarketEvent testEvent;

    @BeforeEach
    void setUp() {
        testEvent = new MarketEvent();
        testEvent.setId(1L);
        testEvent.setName("测试文创市集");
        testEvent.setDescription("这是一个测试活动");
        testEvent.setStatus(MarketEventStatus.DRAFT);
        testEvent.setStartTime(LocalDateTime.of(2024, 1, 1, 9, 0));
        testEvent.setEndTime(LocalDateTime.of(2024, 1, 1, 18, 0));
    }

    @Test
    void saveMarketEvent_Success() {
        // Arrange
        when(marketEventMapper.insert(any(MarketEvent.class))).thenReturn(1);

        // Act
        boolean result = marketEventService.saveMarketEvent(testEvent);

        // Assert
        assertTrue(result);
        verify(marketEventMapper).insert(testEvent);
    }

    @Test
    void updateMarketEvent_Success() {
        // Arrange
        when(marketEventMapper.updateById(any(MarketEvent.class))).thenReturn(1);

        // Act
        boolean result = marketEventService.updateMarketEvent(testEvent);

        // Assert
        assertTrue(result);
        verify(marketEventMapper).updateById(testEvent);
    }

    @Test
    void deleteMarketEvent_Success() {
        // Arrange
        when(marketEventMapper.deleteById(anyLong())).thenReturn(1);

        // Act
        boolean result = marketEventService.deleteMarketEvent(1L);

        // Assert
        assertTrue(result);
        verify(marketEventMapper).deleteById(1L);
    }

    @Test
    void getMarketEventById_Success() {
        // Arrange
        when(marketEventMapper.selectById(anyLong())).thenReturn(testEvent);

        // Act
        MarketEvent result = marketEventService.getMarketEventById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("测试文创市集", result.getName());
        verify(marketEventMapper).selectById(1L);
    }

    @Test
    void getMarketEventsPage_Success() {
        // Arrange
        Page<MarketEvent> page = new Page<>(1, 10);
        List<MarketEvent> events = Arrays.asList(testEvent);
        page.setRecords(events);
        
        when(marketEventMapper.selectPage(any(Page.class), any(LambdaQueryWrapper.class)))
            .thenReturn(page);

        // Act
        Page<MarketEvent> result = marketEventService.getMarketEventsPage(1, 10, null, null);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotal());
        assertEquals("测试文创市集", result.getRecords().get(0).getName());
        verify(marketEventMapper).selectPage(any(Page.class), any(LambdaQueryWrapper.class));
    }

    @Test
    void updateEventStatus_Success() {
        // Arrange
        when(marketEventMapper.updateById(any(MarketEvent.class))).thenReturn(1);

        // Act
        boolean result = marketEventService.updateEventStatus(1L, MarketEventStatus.ACTIVE);

        // Assert
        assertTrue(result);
        verify(marketEventMapper).updateById(any(MarketEvent.class));
    }

    @Test
    void isValidEventTime_Valid() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 18, 0);

        // Act
        boolean result = marketEventService.isValidEventTime(startTime, endTime);

        // Assert
        assertTrue(result);
    }

    @Test
    void isValidEventTime_Invalid() {
        // Arrange
        LocalDateTime startTime = LocalDateTime.of(2024, 1, 1, 18, 0);
        LocalDateTime endTime = LocalDateTime.of(2024, 1, 1, 9, 0);

        // Act
        boolean result = marketEventService.isValidEventTime(startTime, endTime);

        // Assert
        assertFalse(result);
    }
}