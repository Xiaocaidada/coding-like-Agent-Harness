package com.craftmarket.vo;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客流统计信息VO
 */
@Data
public class VisitorStatisticsVO {

    /**
     * 统计ID
     */
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 统计日期
     */
    private LocalDate statisticsDate;

    /**
     * 到访人数
     */
    private Integer visitorCount;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}