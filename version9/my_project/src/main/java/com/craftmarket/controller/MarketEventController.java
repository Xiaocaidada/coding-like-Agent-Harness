package com.craftmarket.controller;

import com.craftmarket.entity.MarketEvent;
import com.craftmarket.service.MarketEventService;
import com.craftmarket.utils.Result;
import com.craftmarket.enums.MarketEventStatus;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/market-events")
@CrossOrigin(origins = "*")
public class MarketEventController {

    @Autowired
    private MarketEventService marketEventService;

    @PostMapping
    public Result<?> createMarketEvent(@RequestBody MarketEvent marketEvent) {
        if (marketEventService.saveMarketEvent(marketEvent)) {
            return Result.success("活动创建成功");
        }
        return Result.error("活动创建失败");
    }

    @GetMapping("/{id}")
    public Result<?> getMarketEventById(@PathVariable Long id) {
        return Result.success(marketEventService.getMarketEventById(id));
    }

    @PutMapping("/{id}")
    public Result<?> updateMarketEvent(@PathVariable Long id, @RequestBody MarketEvent marketEvent) {
        marketEvent.setId(id);
        if (marketEventService.updateMarketEvent(marketEvent)) {
            return Result.success("活动更新成功");
        }
        return Result.error("活动更新失败");
    }

    @GetMapping
    public Result<?> getAllMarketEvents(@RequestParam(defaultValue = "1") int page,
                                      @RequestParam(defaultValue = "10") int size,
                                      @RequestParam(required = false) String eventName,
                                      @RequestParam(required = false) String status) {
        MarketEvent.MarketEventStatus statusEnum = status != null ? MarketEvent.MarketEventStatus.valueOf(status) : null;
        Page<MarketEvent> events = marketEventService.getMarketEventsPage(page, size, eventName, statusEnum);
        return Result.success(events);
    }

    @GetMapping("/active")
    public Result<?> getActiveMarketEvents(@RequestParam(defaultValue = "1") int page,
                                          @RequestParam(defaultValue = "10") int size) {
        Page<MarketEvent> pageObj = new Page<>(page, size);
        Page<MarketEvent> events = marketEventService.getMarketEventsByStatus(MarketEvent.MarketEventStatus.ACTIVE, pageObj);
        return Result.success(events);
    }

    @GetMapping("/upcoming")
    public Result<?> getUpcomingMarketEvents(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        // 这里可以根据实际需求过滤未来的活动
        return Result.success(new Page<>());
    }

    @PutMapping("/{id}/publish")
    public Result<?> publishMarketEvent(@PathVariable Long id) {
        if (marketEventService.updateEventStatus(id, MarketEventStatus.ACTIVE)) {
            return Result.success("活动发布成功");
        }
        return Result.error("活动发布失败");
    }

    @PutMapping("/{id}/cancel")
    public Result<?> cancelMarketEvent(@PathVariable Long id) {
        if (marketEventService.updateEventStatus(id, MarketEventStatus.CANCELLED)) {
            return Result.success("活动取消成功");
        }
        return Result.error("活动取消失败");
    }
}