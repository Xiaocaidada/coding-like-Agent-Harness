package com.craftmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.craftmarket.entity.Booth;
import com.craftmarket.entity.MarketEvent;
import com.craftmarket.enums.MarketEventStatus;
import com.craftmarket.mapper.MarketEventMapper;
import com.craftmarket.service.BoothService;
import com.craftmarket.service.MarketEventService;
import com.craftmarket.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class MarketEventServiceImpl extends ServiceImpl<MarketEventMapper, MarketEvent> implements MarketEventService {

    @Autowired
    private BoothService boothService;

    @Override
    @Transactional
    public boolean saveMarketEvent(MarketEvent marketEvent) {
        // 验证活动时间
        if (!isValidEventTime(marketEvent.getStartTime(), marketEvent.getEndTime())) {
            throw new IllegalArgumentException("活动时间不合法，结束时间必须晚于开始时间");
        }
        return save(marketEvent);
    }

    @Override
    @Transactional
    public boolean updateMarketEvent(MarketEvent marketEvent) {
        // 验证活动时间
        if (!isValidEventTime(marketEvent.getStartTime(), marketEvent.getEndTime())) {
            throw new IllegalArgumentException("活动时间不合法，结束时间必须晚于开始时间");
        }
        return updateById(marketEvent);
    }

    @Override
    @Transactional
    public boolean deleteMarketEvent(Long id) {
        // 检查是否有相关摊位已分配
        long assignedBoothCount = boothService.lambdaQuery()
                .eq(Booth::getMarketEventId, id)
                .eq(Booth::getStatus, Booth.BoothStatus.ASSIGNED)
                .count();
        
        if (assignedBoothCount > 0) {
            throw new IllegalStateException("该活动已有分配的摊位，无法删除");
        }
        
        return removeById(id);
    }

    @Override
    public MarketEvent getMarketEventById(Long id) {
        return getById(id);
    }

    @Override
    public Page<MarketEvent> getMarketEventsPage(Integer current, Integer size, String eventName, MarketEvent.MarketEventStatus status) {
        Page<MarketEvent> page = new Page<>(current, size);
        LambdaQueryWrapper<MarketEvent> queryWrapper = new LambdaQueryWrapper<>();
        
        if (eventName != null && !eventName.trim().isEmpty()) {
            queryWrapper.like(MarketEvent::getEventName, eventName);
        }
        
        if (status != null) {
            queryWrapper.eq(MarketEvent::getStatus, status);
        }
        
        queryWrapper.orderByDesc(MarketEvent::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    public Page<MarketEvent> getMarketEventsByStatus(MarketEvent.MarketEventStatus status, Page<MarketEvent> page) {
        return lambdaQuery()
                .eq(MarketEvent::getStatus, status)
                .orderByDesc(MarketEvent::getCreateTime)
                .page(page);
    }

    @Override
    public Page<Booth> getAvailableBoothsByEvent(Long eventId, String area, Page<Booth> page) {
        return boothService.lambdaQuery()
                .eq(Booth::getMarketEventId, eventId)
                .eq(Booth::getStatus, Booth.BoothStatus.AVAILABLE)
                .like(area != null && !area.trim().isEmpty(), Booth::getArea, area)
                .page(page);
    }

    @Override
    @Transactional
    public boolean updateEventStatus(Long id, MarketEventStatus status) {
        MarketEvent marketEvent = getById(id);
        if (marketEvent == null) {
            throw new RuntimeException("活动不存在");
        }
        marketEvent.setStatus(status);
        return updateById(marketEvent);
    }

    @Override
    public boolean isValidEventTime(LocalDateTime startTime, LocalDateTime endTime) {
        return startTime != null && endTime != null && startTime.isBefore(endTime);
    }
}