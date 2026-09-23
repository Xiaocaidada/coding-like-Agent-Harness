package com.craftmarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.craftmarket.entity.MarketEvent;
import com.craftmarket.entity.Booth;
import com.craftmarket.entity.MarketEvent.MarketEventStatus;

import java.time.LocalDateTime;

public interface MarketEventService {

    // 保存活动
    boolean saveMarketEvent(MarketEvent marketEvent);

    // 更新活动
    boolean updateMarketEvent(MarketEvent marketEvent);

    // 删除活动
    boolean deleteMarketEvent(Long id);

    // 根据ID查询活动
    MarketEvent getMarketEventById(Long id);

    // 分页查询活动列表
    Page<MarketEvent> getMarketEventsPage(Integer current, Integer size, String eventName, MarketEventStatus status);

    // 根据状态查询活动列表
    Page<MarketEvent> getMarketEventsByStatus(MarketEventStatus status, Page<MarketEvent> page);

    // 根据活动ID查询可用摊位
    Page<Booth> getAvailableBoothsByEvent(Long eventId, String area, Page<Booth> page);

    // 更新活动状态
    boolean updateEventStatus(Long id, MarketEventStatus status);

    // 检查活动时间是否有效
    boolean isValidEventTime(LocalDateTime startTime, LocalDateTime endTime);
}