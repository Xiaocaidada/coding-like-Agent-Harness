package com.craftmarket.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;

/**
 * 订单创建请求DTO
 */
@Data
public class OrderCreateRequest {

    /**
     * 活动ID
     */
    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    /**
     * 摊主报名ID
     */
    @NotNull(message = "摊主报名ID不能为空")
    private Long vendorApplicationId;

    /**
     * 摊位ID
     */
    @NotNull(message = "摊位ID不能为空")
    private Long boothId;

    /**
     * 订单金额
     */
    @NotNull(message = "订单金额不能为空")
    @Min(value = 0, message = "订单金额不能小于0")
    private BigDecimal amount;
}