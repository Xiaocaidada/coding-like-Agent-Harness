package com.craftmarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.craftmarket.entity.MarketActivity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 市集活动Mapper接口
 */
@Mapper
public interface MarketActivityMapper extends BaseMapper<MarketActivity> {

    /**
     * 根据状态查询活动列表
     *
     * @param status 状态
     * @return 活动列表
     */
    List<MarketActivity> selectByStatus(@Param("status") String status);

    /**
     * 根据活动时间范围查询活动
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @return 活动列表
     */
    List<MarketActivity> selectByTimeRange(@Param("startTime") LocalDateTime startTime, 
                                           @Param("endTime") LocalDateTime endTime);

    /**
     * 更新活动状态
     *
     * @param id     活动ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}