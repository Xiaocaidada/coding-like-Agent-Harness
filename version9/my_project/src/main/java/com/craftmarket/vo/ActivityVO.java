package com.craftmarket.vo;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 活动信息VO
 */
@Data
public class ActivityVO {

    /**
     * 活动ID
     */
    private Long id;

    /**
     * 活动名称
     */
    private String name;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 活动地点
     */
    private String location;

    /**
     * 活动简介
     */
    private String description;

    /**
     * 海报URL
     */
    private String posterUrl;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}