package com.craftmarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 摊主报名实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("vendor_application")
public class VendorApplication {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 活动ID
     */
    @TableField("activity_id")
    private Long activityId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;

    /**
     * 摊主姓名
     */
    @TableField("vendor_name")
    private String vendorName;

    /**
     * 联系电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 经营品类
     */
    @TableField("business_type")
    private String businessType;

    /**
     * 简介
     */
    @TableField("description")
    private String description;

    /**
     * 期望摊位区域
     */
    @TableField("preferred_area")
    private String preferredArea;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 驳回原因
     */
    @TableField("reject_reason")
    private String rejectReason;

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