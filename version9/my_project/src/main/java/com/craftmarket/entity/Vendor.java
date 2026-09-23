package com.craftmarket.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = false)
@TableName("vendor")
public class Vendor {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String phone;

    private String businessType;

    private String description;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}