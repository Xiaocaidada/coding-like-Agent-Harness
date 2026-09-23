package com.craftmarket.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 摊主报名请求DTO
 */
@Data
public class VendorApplicationRequest {

    /**
     * 活动ID
     */
    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    /**
     * 摊主姓名
     */
    @NotBlank(message = "摊主姓名不能为空")
    private String vendorName;

    /**
     * 联系电话
     */
    @NotBlank(message = "联系电话不能为空")
    private String phone;

    /**
     * 经营品类
     */
    @NotBlank(message = "经营品类不能为空")
    private String businessType;

    /**
     * 简介
     */
    private String description;

    /**
     * 期望摊位区域
     */
    private String preferredArea;
}