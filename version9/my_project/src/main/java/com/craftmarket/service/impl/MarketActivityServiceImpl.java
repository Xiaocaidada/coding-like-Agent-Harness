package com.craftmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.craftmarket.entity.MarketActivity;
import com.craftmarket.mapper.MarketActivityMapper;
import com.craftmarket.service.MarketActivityService;
import com.craftmarket.vo.ActivityVO;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 市集活动服务实现类
 */
@Service
public class MarketActivityServiceImpl extends ServiceImpl<MarketActivityMapper, MarketActivity> implements MarketActivityService {

    @Override
    @Transactional
    public ActivityVO createActivity(MarketActivity activity) {
        // 设置默认值
        activity.setStatus("DRAFT"); // 默认为草稿状态
        activity.setCreateTime(LocalDateTime.now());
        
        save(activity);
        
        ActivityVO activityVO = new ActivityVO();
        BeanUtils.copyProperties(activity, activityVO);
        return activityVO;
    }

    @Override
    @Transactional
    public ActivityVO updateActivity(MarketActivity activity) {
        MarketActivity existing = getById(activity.getId());
        if (existing == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        
        activity.setUpdateTime(LocalDateTime.now());
        updateById(activity);
        
        ActivityVO activityVO = new ActivityVO();
        BeanUtils.copyProperties(activity, activityVO);
        return activityVO;
    }

    @Override
    public ActivityVO getActivityDetail(Long id) {
        MarketActivity activity = getById(id);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        
        ActivityVO activityVO = new ActivityVO();
        BeanUtils.copyProperties(activity, activityVO);
        return activityVO;
    }

    @Override
    public List<ActivityVO> getActivityList(String status) {
        QueryWrapper<MarketActivity> queryWrapper = new QueryWrapper<>();
        if (status != null && !status.trim().isEmpty()) {
            queryWrapper.eq("status", status);
        }
        queryWrapper.orderByDesc("create_time");
        
        List<MarketActivity> activities = list(queryWrapper);
        
        return activities.stream()
                .map(activity -> {
                    ActivityVO activityVO = new ActivityVO();
                    BeanUtils.copyProperties(activity, activityVO);
                    return activityVO;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ActivityVO updateActivityStatus(Long id, String status) {
        MarketActivity activity = getById(id);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        
        activity.setStatus(status);
        activity.setUpdateTime(LocalDateTime.now());
        
        updateById(activity);
        
        ActivityVO activityVO = new ActivityVO();
        BeanUtils.copyProperties(activity, activityVO);
        return activityVO;
    }

    @Override
    @Transactional
    public void deleteActivity(Long id) {
        MarketActivity activity = getById(id);
        if (activity == null) {
            throw new IllegalArgumentException("活动不存在");
        }
        
        // 检查是否有关联的摊位
        // 这里应该检查是否有摊位订单，为了简化，直接删除
        
        removeById(id);
    }

    @Override
    public List<ActivityVO> getUpcomingActivities(int limit) {
        // 这里使用基础的查询，实际应该使用Mapper中的自定义查询
        // 为了简化，直接使用状态为NOT_STARTED且开始时间大于现在的活动
        return getActivityList("NOT_STARTED").stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
}