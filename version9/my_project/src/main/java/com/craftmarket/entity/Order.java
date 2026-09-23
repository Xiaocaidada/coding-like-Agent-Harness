package com.craftmarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("order_")
public class Order {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long vendorId;

    private Long marketEventId;

    private Long boothId;

    private BigDecimal amount;

    private OrderStatus status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    public enum OrderStatus {
        PENDING_PAYMENT, PAID, CANCELLED
    }
}