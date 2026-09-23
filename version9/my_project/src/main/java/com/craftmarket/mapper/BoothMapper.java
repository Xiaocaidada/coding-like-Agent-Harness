package com.craftmarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.craftmarket.entity.Booth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 摊位Mapper接口
 */
@Mapper
public interface BoothMapper extends BaseMapper<Booth> {

    /**
     * 根据活动ID查询摊位列表
     *
     * @param activityId 活动ID
     * @return 摊位列表
     */
    List<Booth> selectByActivityId(@Param("activityId") Long activityId);

    /**
     * 根据活动ID和状态查询摊位列表
     *
     * @param activityId 活动ID
     * @param status     状态
     * @return 摊位列表
     */
    List<Booth> selectByActivityIdAndStatus(@Param("activityId") Long activityId, 
                                           @Param("status") String status);

    /**
     * 根据区域查询摊位列表
     *
     * @param activityId 活动ID
     * @param area       区域
     * @return 摊位列表
     */
    List<Booth> selectByActivityIdAndArea(@Param("activityId") Long activityId, 
                                         @Param("area") String area);

    /**
     * 更新摊位状态
     *
     * @param id     摊位ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") String status);
}