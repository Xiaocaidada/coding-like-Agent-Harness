package com.craftmarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.craftmarket.entity.VisitorStatistics;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

/**
 * 客流统计Mapper接口
 */
@Mapper
public interface VisitorStatisticsMapper extends BaseMapper<VisitorStatistics> {

    /**
     * 根据活动ID和统计日期查询客流统计
     *
     * @param activityId       活动ID
     * @param statisticsDate   统计日期
     * @return 客流统计信息
     */
    VisitorStatistics selectByActivityIdAndDate(@Param("activityId") Long activityId, 
                                             @Param("statisticsDate") LocalDate statisticsDate);

    /**
     * 根据活动ID查询客流统计列表
     *
     * @param activityId 活动ID
     * @return 客流统计列表
     */
    List<VisitorStatistics> selectByActivityId(@Param("activityId") Long activityId);

    /**
     * 根据活动ID和日期范围查询客流统计
     *
     * @param activityId 活动ID
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @return 客流统计列表
     */
    List<VisitorStatistics> selectByActivityIdAndDateRange(@Param("activityId") Long activityId, 
                                                        @Param("startDate") LocalDate startDate, 
                                                        @Param("endDate") LocalDate endDate);

    /**
     * 批量插入客流统计数据
     *
     * @param activityId 活动ID
     * @param dateList   日期列表
     * @param visitorCount 到访人数
     * @return 影响行数
     */
    int batchInsertStatistics(@Param("activityId") Long activityId, 
                            @Param("dateList") List<LocalDate> dateList,
                            @Param("visitorCount") Integer visitorCount);
}