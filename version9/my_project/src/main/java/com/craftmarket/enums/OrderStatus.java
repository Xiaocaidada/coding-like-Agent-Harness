package com.craftmarket.enums;

/**
 * 订单状态枚举
 */
public enum OrderStatus {
    PENDING_PAYMENT("PENDING_PAYMENT", "待支付"),
    PAID("PAID", "已支付"),
    CANCELLED("CANCELLED", "已取消");

    private final String code;
    private final String desc;

    OrderStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static OrderStatus fromCode(String code) {
        for (OrderStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status code: " + code);
    }

    public static OrderStatus fromDesc(String desc) {
        for (OrderStatus status : values()) {
            if (status.desc.equals(desc)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid order status desc: " + desc);
    }
}