package com.craftmarket.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 摊主报名信息VO
 */
@Data
public class VendorApplicationVO {

    /**
     * 报名ID
     */
    private Long id;

    /**
     * 活动ID
     */
    private Long activityId;

    /**
     * 摊主姓名
     */
    private String vendorName;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 经营品类
     */
    private String businessType;

    /**
     * 简介
     */
    private String description;

    /**
     * 期望摊位区域
     */
    private String preferredArea;

    /**
     * 状态
     */
    private String status;

    /**
     * 驳回原因
     */
    private String rejectReason;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}