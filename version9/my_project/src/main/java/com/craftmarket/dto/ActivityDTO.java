package com.craftmarket.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * 活动DTO
 */
@Data
public class ActivityDTO {

    /**
     * 活动名称
     */
    @NotBlank(message = "活动名称不能为空")
    private String name;

    /**
     * 开始时间
     */
    @NotNull(message = "开始时间不能为空")
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    @NotNull(message = "结束时间不能为空")
    private LocalDateTime endTime;

    /**
     * 活动地点
     */
    @NotBlank(message = "活动地点不能为空")
    private String location;

    /**
     * 活动简介
     */
    private String description;

    /**
     * 海报URL
     */
    private String posterUrl;
}