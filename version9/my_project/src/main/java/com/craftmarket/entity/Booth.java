package com.craftmarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 摊位实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("booth")
public class Booth {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 摊位编号
     */
    @TableField("booth_number")
    private String boothNumber;

    /**
     * 所在区域
     */
    @TableField("area")
    private String area;

    /**
     * 区域名称
     */
    @TableField("area_name")
    private String areaName;

    /**
     * 面积
     */
    @TableField("area_size")
    private BigDecimal areaSize;

    /**
     * 租金价格
     */
    @TableField("rent_price")
    private BigDecimal rentPrice;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 所属活动ID
     */
    @TableField("activity_id")
    private Long activityId;

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