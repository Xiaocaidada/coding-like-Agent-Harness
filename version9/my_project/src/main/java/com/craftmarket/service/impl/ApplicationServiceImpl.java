package com.craftmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.craftmarket.entity.Application;
import com.craftmarket.service.ApplicationService;
import com.craftmarket.service.MarketEventService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Slf4j
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    @Autowired
    private MarketEventService marketEventService;

    @Override
    @Transactional
    public boolean submitApplication(Application application) {
        // 验证活动是否存在
        if (marketEventService.getMarketEventById(application.getMarketEventId()) == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        
        // 检查是否已经报名过
        if (hasAppliedToEvent(application.getVendorId(), application.getMarketEventId())) {
            throw new IllegalStateException("该摊主已经报名过该活动");
        }
        
        application.setStatus(ApplicationStatus.PENDING);
        application.setCreateTime(LocalDateTime.now());
        application.setUpdateTime(LocalDateTime.now());
        
        return save(application);
    }

    @Override
    public Application getApplicationById(Long id) {
        return getById(id);
    }

    @Override
    public Page<Application> getApplicationsPage(Integer current, Integer size, Long eventId, ApplicationStatus status) {
        Page<Application> page = new Page<>(current, size);
        LambdaQueryWrapper<Application> queryWrapper = new LambdaQueryWrapper<>();
        
        if (eventId != null) {
            queryWrapper.eq(Application::getMarketEventId, eventId);
        }
        
        if (status != null) {
            queryWrapper.eq(Application::getStatus, status);
        }
        
        queryWrapper.orderByDesc(Application::getCreateTime);
        return page(page, queryWrapper);
    }

    @Override
    public Page<Application> getApplicationsByVendor(Long vendorId, Page<Application> page) {
        return lambdaQuery()
                .eq(Application::getVendorId, vendorId)
                .orderByDesc(Application::getCreateTime)
                .page(page);
    }

    @Override
    public Application getApplicationByVendorAndEvent(Long vendorId, Long eventId) {
        return lambdaQuery()
                .eq(Application::getVendorId, vendorId)
                .eq(Application::getMarketEventId, eventId)
                .one();
    }

    @Override
    @Transactional
    public boolean reviewApplication(Long applicationId, ApplicationStatus status, String rejectReason) {
        Application application = getById(applicationId);
        if (application == null) {
            throw new IllegalArgumentException("报名申请不存在");
        }
        
        // 如果是驳回，必须有驳回原因
        if (ApplicationStatus.REJECTED.equals(status) && (rejectReason == null || rejectReason.trim().isEmpty())) {
            throw new IllegalArgumentException("驳回原因不能为空");
        }
        
        application.setStatus(status);
        application.setUpdateTime(LocalDateTime.now());
        
        if (ApplicationStatus.REJECTED.equals(status)) {
            application.setRejectReason(rejectReason);
        }
        
        return updateById(application);
    }

    @Override
    @Transactional
    public boolean deleteApplication(Long id) {
        return removeById(id);
    }

    @Override
    public boolean hasAppliedToEvent(Long vendorId, Long eventId) {
        Application existingApplication = getApplicationByVendorAndEvent(vendorId, eventId);
        return existingApplication != null && !ApplicationStatus.REJECTED.equals(existingApplication.getStatus());
    }
}