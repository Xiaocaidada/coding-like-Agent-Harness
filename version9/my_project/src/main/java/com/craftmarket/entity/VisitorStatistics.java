package com.craftmarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 客流统计实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("visitor_statistics")
public class VisitorStatistics {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动ID
     */
    @TableField("activity_id")
    private Long activityId;

    /**
     * 统计日期
     */
    @TableField("statistics_date")
    private LocalDate statisticsDate;

    /**
     * 到访人数
     */
    @TableField("visitor_count")
    private Integer visitorCount;

    /**
     * 创建时间
     */
    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}