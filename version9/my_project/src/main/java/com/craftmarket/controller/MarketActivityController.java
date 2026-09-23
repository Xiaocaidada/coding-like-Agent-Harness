package com.craftmarket.controller;

import com.craftmarket.entity.MarketActivity;
import com.craftmarket.service.MarketActivityService;
import com.craftmarket.vo.ActivityVO;
import com.craftmarket.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 市集活动控制器
 */
@RestController
@RequestMapping("/api/market-activities")
@Tag(name = "市集活动管理", description = "市集活动的增删改查接口")
public class MarketActivityController {

    @Autowired
    private MarketActivityService marketActivityService;

    @PostMapping
    @Operation(summary = "创建活动", description = "创建新的市集活动")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ActivityVO> createActivity(@Valid @RequestBody MarketActivity activity) {
        ActivityVO result = marketActivityService.createActivity(activity);
        return Result.success(result);
    }

    @PutMapping("/{id}")
    @Operation(summary = "更新活动", description = "更新指定的市集活动信息")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ActivityVO> updateActivity(
            @Parameter(description = "活动ID") @PathVariable Long id,
            @Valid @RequestBody MarketActivity activity) {
        activity.setId(id);
        ActivityVO result = marketActivityService.updateActivity(activity);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取活动详情", description = "根据ID获取指定活动的详细信息")
    public Result<ActivityVO> getActivityDetail(
            @Parameter(description = "活动ID") @PathVariable Long id) {
        ActivityVO result = marketActivityService.getActivityDetail(id);
        return Result.success(result);
    }

    @GetMapping
    @Operation(summary = "获取活动列表", description = "获取所有市集活动列表，可根据状态筛选")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    public Result<List<ActivityVO>> getActivityList(
            @Parameter(description = "活动状态筛选") @RequestParam(required = false) String status) {
        List<ActivityVO> result = marketActivityService.getActivityList(status);
        return Result.success(result);
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "更新活动状态", description = "更新市集活动的状态")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<ActivityVO> updateActivityStatus(
            @Parameter(description = "活动ID") @PathVariable Long id,
            @Parameter(description = "新状态") @RequestParam String status) {
        ActivityVO result = marketActivityService.updateActivityStatus(id, status);
        return Result.success(result);
    }

    @GetMapping("/upcoming")
    @Operation(summary = "获取即将开始的活动", description = "获取即将开始的活动列表")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    public Result<List<ActivityVO>> getUpcomingActivities(
            @Parameter(description = "限制数量") @RequestParam(defaultValue = "10") int limit) {
        List<ActivityVO> result = marketActivityService.getUpcomingActivities(limit);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除活动", description = "删除指定的市集活动")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteActivity(
            @Parameter(description = "活动ID") @PathVariable Long id) {
        marketActivityService.deleteActivity(id);
        return Result.success();
    }
}