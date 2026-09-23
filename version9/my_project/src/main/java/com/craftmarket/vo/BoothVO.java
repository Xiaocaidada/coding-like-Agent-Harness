package com.craftmarket.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 摊位信息VO
 */
@Data
public class BoothVO {

    /**
     * 摊位ID
     */
    private Long id;

    /**
     * 摊位编号
     */
    private String boothNumber;

    /**
     * 所在区域
     */
    private String area;

    /**
     * 区域名称
     */
    private String areaName;

    /**
     * 面积
     */
    private BigDecimal areaSize;

    /**
     * 租金价格
     */
    private BigDecimal rentPrice;

    /**
     * 状态
     */
    private String status;

    /**
     * 所属活动ID
     */
    private Long activityId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}