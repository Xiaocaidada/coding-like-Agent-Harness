package com.craftmarket.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.craftmarket.entity.Application;
import com.craftmarket.service.ApplicationService;
import com.craftmarket.utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 报名申请控制器
 */
@RestController
@RequestMapping("/api/applications")
@Tag(name = "报名申请管理", description = "市集活动报名申请的增删改查接口")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping
    @Operation(summary = "提交报名申请", description = "摊主向市集活动提交报名申请")
    @PreAuthorize("hasRole('VENDOR')")
    public Result<Boolean> submitApplication(@Valid @RequestBody Application application) {
        boolean result = applicationService.submitApplication(application);
        return Result.success(result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "获取报名详情", description = "根据ID获取指定的报名申请详情")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    public Result<Application> getApplicationDetail(
            @Parameter(description = "报名ID") @PathVariable Long id) {
        Application result = applicationService.getApplicationById(id);
        return Result.success(result);
    }

    @GetMapping("/admin/page")
    @Operation(summary = "分页查询报名列表（管理员）", description = "管理员分页查询活动报名列表")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Page<Application>> getApplicationsPage(
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "活动ID筛选") @RequestParam(required = false) Long eventId,
            @Parameter(description = "状态筛选") @RequestParam(required = false) Application.ApplicationStatus status) {
        Page<Application> result = applicationService.getApplicationsPage(current, size, eventId, status);
        return Result.success(result);
    }

    @GetMapping("/vendor/{vendorId}")
    @Operation(summary = "根据摊主查询报名列表", description = "根据摊主ID查询其所有报名申请")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    public Result<Page<Application>> getApplicationsByVendor(
            @Parameter(description = "摊主ID") @PathVariable Long vendorId,
            @Parameter(description = "当前页码") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {
        Page<Application> page = new Page<>(current, size);
        Page<Application> result = applicationService.getApplicationsByVendor(vendorId, page);
        return Result.success(result);
    }

    @PutMapping("/{id}/review")
    @Operation(summary = "审核报名申请", description = "管理员审核摊主的报名申请")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> reviewApplication(
            @Parameter(description = "报名ID") @PathVariable Long id,
            @Parameter(description = "审核状态") @RequestParam Application.ApplicationStatus status,
            @Parameter(description = "驳回原因") @RequestParam(required = false) String rejectReason) {
        boolean result = applicationService.reviewApplication(id, status, rejectReason);
        return Result.success(result);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除报名申请", description = "删除指定的报名申请")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Boolean> deleteApplication(
            @Parameter(description = "报名ID") @PathVariable Long id) {
        boolean result = applicationService.deleteApplication(id);
        return Result.success(result);
    }

    @GetMapping("/check-application")
    @Operation(summary = "检查报名状态", description = "检查摊主是否已经报名过指定活动")
    @PreAuthorize("hasAnyRole('ADMIN', 'VENDOR')")
    public Result<Boolean> hasAppliedToEvent(
            @Parameter(description = "摊主ID") @RequestParam Long vendorId,
            @Parameter(description = "活动ID") @RequestParam Long eventId) {
        boolean result = applicationService.hasAppliedToEvent(vendorId, eventId);
        return Result.success(result);
    }
}