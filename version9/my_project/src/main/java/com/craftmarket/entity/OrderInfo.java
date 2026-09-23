package com.craftmarket.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_info")
public class OrderInfo {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 订单编号
     */
    @TableField("order_no")
    private String orderNo;

    /**
     * 活动ID
     */
    @TableField("activity_id")
    private Long activityId;

    /**
     * 摊主报名ID
     */
    @TableField("vendor_application_id")
    private Long vendorApplicationId;

    /**
     * 摊位ID
     */
    @TableField("booth_id")
    private Long boothId;

    /**
     * 订单金额
     */
    @TableField("amount")
    private BigDecimal amount;

    /**
     * 状态
     */
    @TableField("status")
    private String status;

    /**
     * 支付时间
     */
    @TableField("payment_time")
    private LocalDateTime paymentTime;

    /**
     * 取消时间
     */
    @TableField("cancel_time")
    private LocalDateTime cancelTime;

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