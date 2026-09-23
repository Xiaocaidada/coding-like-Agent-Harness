package com.craftmarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.craftmarket.entity.Application;

public interface ApplicationService {

    // 提交报名申请
    boolean submitApplication(Application application);

    // 根据ID查询报名
    Application getApplicationById(Long id);

    // 分页查询报名列表（管理员）
    Page<Application> getApplicationsPage(Integer current, Integer size, Long eventId, ApplicationStatus status);

    // 根据摊主ID查询报名
    Page<Application> getApplicationsByVendor(Long vendorId, Page<Application> page);

    // 根据摊主和活动查询报名
    Application getApplicationByVendorAndEvent(Long vendorId, Long eventId);

    // 审核报名申请
    boolean reviewApplication(Long applicationId, ApplicationStatus status, String rejectReason);

    // 删除报名申请
    boolean deleteApplication(Long id);

    // 检查是否已经报名过该活动
    boolean hasAppliedToEvent(Long vendorId, Long eventId);

    // 应用状态枚举
    enum ApplicationStatus {
        PENDING, APPROVED, REJECTED
    }
}