package com.craftmarket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.craftmarket.entity.MarketActivity;
import com.craftmarket.vo.ActivityVO;

import java.util.List;

/**
 * 市集活动服务接口
 */
public interface MarketActivityService extends IService<MarketActivity> {

    /**
     * 创建活动
     *
     * @param activity 活动信息
     * @return 创建的活动信息
     */
    ActivityVO createActivity(MarketActivity activity);

    /**
     * 更新活动信息
     *
     * @param activity 活动信息
     * @return 更新后的活动信息
     */
    ActivityVO updateActivity(MarketActivity activity);

    /**
     * 获取活动详情
     *
     * @param id 活动ID
     * @return 活动信息
     */
    ActivityVO getActivityDetail(Long id);

    /**
     * 获取活动列表
     *
     * @param status 状态（可选）
     * @return 活动列表
     */
    List<ActivityVO> getActivityList(String status);

    /**
     * 更新活动状态
     *
     * @param id     活动ID
     * @param status 状态
     * @return 更新后的活动信息
     */
    ActivityVO updateActivityStatus(Long id, String status);

    /**
     * 删除活动
     *
     * @param id 活动ID
     */
    void deleteActivity(Long id);

    /**
     * 获取即将开始的活动
     *
     * @param limit 限制数量
     * @return 即将开始的活动列表
     */
    List<ActivityVO> getUpcomingActivities(int limit);
}