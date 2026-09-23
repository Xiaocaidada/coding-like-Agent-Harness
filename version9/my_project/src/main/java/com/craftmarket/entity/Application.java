package com.craftmarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("application")
public class Application {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long vendorId;

    private Long marketEventId;

    private String expectedArea;

    private ApplicationStatus status;

    private String rejectReason;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public enum ApplicationStatus {
        PENDING, APPROVED, REJECTED
    }
}