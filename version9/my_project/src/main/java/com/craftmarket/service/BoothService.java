package com.craftmarket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.craftmarket.entity.Booth;
import com.craftmarket.vo.BoothVO;

import java.util.List;

/**
 * 摊位服务接口
 */
public interface BoothService extends IService<Booth> {

    /**
     * 创建摊位
     *
     * @param booth 摊位信息
     * @return 创建的摊位信息
     */
    BoothVO createBooth(Booth booth);

    /**
     * 批量创建摊位
     *
     * @param activityId 活动ID
     * @param area       区域
     * @param areaName   区域名称
     * @param areaSize   面积
     * @param rentPrice  租金
     * @param boothCount 摊位数量
     * @return 创建的摊位数量
     */
    int batchCreateBooths(Long activityId, String area, String areaName, 
                         java.math.BigDecimal areaSize, java.math.BigDecimal rentPrice, 
                         int boothCount);

    /**
     * 获取活动下的所有摊位
     *
     * @param activityId 活动ID
     * @return 摊位列表
     */
    List<BoothVO> getBoothsByActivity(Long activityId);

    /**
     * 根据区域获取摊位
     *
     * @param activityId 活动ID
     * @param area       区域
     * @return 摊位列表
     */
    List<BoothVO> getBoothsByArea(Long activityId, String area);

    /**
     * 获取可用的摊位列表
     *
     * @param activityId 活动ID
     * @return 可用摊位列表
     */
    List<BoothVO> getAvailableBooths(Long activityId);

    /**
     * 更新摊位状态
     *
     * @param id     摊位ID
     * @param status 状态
     * @return 更新后的摊位信息
     */
    BoothVO updateBoothStatus(Long id, String status);

    /**
     * 分配摊位给摊主报名
     *
     * @param boothId          摊位ID
     * @param vendorApplicationId 摊主报名ID
     * @return 是否成功
     */
    boolean assignBoothToVendor(Long boothId, Long vendorApplicationId);
}