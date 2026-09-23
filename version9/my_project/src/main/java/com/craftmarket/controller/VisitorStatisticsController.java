package com.craftmarket.controller;

import com.craftmarket.entity.VisitorStatistics;
import com.craftmarket.service.VisitorStatisticsService;
import com.craftmarket.utils.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/visitor-statistics")
@CrossOrigin(origins = "*")
public class VisitorStatisticsController {

    @Autowired
    private VisitorStatisticsService visitorStatisticsService;

    @PostMapping
    public Result<?> createVisitorStatistics(@RequestBody VisitorStatistics visitorStatistics) {
        return visitorStatisticsService.createVisitorStatistics(visitorStatistics);
    }

    @GetMapping("/{id}")
    public Result<?> getVisitorStatisticsById(@PathVariable Long id) {
        return Result.success(visitorStatisticsService.getVisitorStatisticsById(id));
    }

    @GetMapping
    public Result<?> getAllVisitorStatistics(@RequestParam(defaultValue = "1") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        Page<VisitorStatistics> statistics = visitorStatisticsService.getAllVisitorStatistics(page, size);
        return Result.success(statistics);
    }

    @GetMapping("/market-event/{marketEventId}")
    public Result<?> getStatisticsByMarketEvent(@PathVariable Long marketEventId,
                                              @RequestParam(defaultValue = "1") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        Page<VisitorStatistics> statistics = visitorStatisticsService.getStatisticsByMarketEvent(marketEventId, page, size);
        return Result.success(statistics);
    }

    @GetMapping("/date-range")
    public Result<?> getStatisticsByDateRange(@RequestParam LocalDate startDate,
                                            @RequestParam LocalDate endDate,
                                            @RequestParam(defaultValue = "1") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Page<VisitorStatistics> statistics = visitorStatisticsService.getStatisticsByDateRange(startDate, endDate, page, size);
        return Result.success(statistics);
    }

    @GetMapping("/booth/{boothId}")
    public Result<?> getStatisticsByBooth(@PathVariable Long boothId,
                                        @RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "10") int size) {
        Page<VisitorStatistics> statistics = visitorStatisticsService.getStatisticsByBooth(boothId, page, size);
        return Result.success(statistics);
    }

    @PostMapping("/batch-update")
    public Result<?> batchUpdateStatistics(@RequestBody java.util.List<VisitorStatistics> statisticsList) {
        return visitorStatisticsService.batchUpdateStatistics(statisticsList);
    }

    @GetMapping("/summary/{marketEventId}")
    public Result<?> getMarketEventSummary(@PathVariable Long marketEventId) {
        return Result.success(visitorStatisticsService.getMarketEventSummary(marketEventId));
    }
}