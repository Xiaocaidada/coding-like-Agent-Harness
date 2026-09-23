package com.craftmarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("market_event")
public class MarketEvent {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String eventName;

    private LocalDateTime eventStartTime;

    private LocalDateTime eventEndTime;

    private String location;

    private String description;

    private String poster;

    private MarketEventStatus status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public enum MarketEventStatus {
        NOT_STARTED, IN_PROGRESS, ENDED
    }
}