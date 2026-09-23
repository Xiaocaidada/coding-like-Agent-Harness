package com.craftmarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 市集活动实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("market_activity")
public class MarketActivity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动名称
     */
    @TableField("name")
    private String name;

    /**
     * 开始时间
     */
    @TableField("start_time")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @TableField("end_time")
    private LocalDateTime endTime;

    /**
     * 活动地点
     */
    @TableField("location")
    private String location;

    /**
     * 活动简介
     */
    @TableField("description")
    private String description;

    /**
     * 海报URL
     */
    @TableField("poster_url")
    private String posterUrl;

    /**
     * 状态
     */
    @TableField("status")
    private ActivityStatus status;

    /**
     * 活动状态枚举
     */
    public enum ActivityStatus {
        NOT_STARTED, IN_PROGRESS, ENDED, CANCELLED
    }

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