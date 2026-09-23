package com.craftmarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 订单信息VO
 */
@Data
public class OrderVO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 摊主报名ID
     */
    private Long vendorApplicationId;

    /**
     * 摊位ID
     */
    private Long boothId;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 状态
     */
    private String status;

    /**
     * 支付时间
     */
    private LocalDateTime paymentTime;

    /**
     * 取消时间
     */
    private LocalDateTime cancelTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}