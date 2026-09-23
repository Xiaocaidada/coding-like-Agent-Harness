package com.craftmarket.enums;

/**
 * 摊位状态枚举
 */
public enum BoothStatus {
    AVAILABLE("AVAILABLE", "空闲"),
    ALLOCATED("ALLOCATED", "已分配"),
    DISABLED("DISABLED", "禁用");

    private final String code;
    private final String desc;

    BoothStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static BoothStatus fromCode(String code) {
        for (BoothStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid booth status code: " + code);
    }

    public static BoothStatus fromDesc(String desc) {
        for (BoothStatus status : values()) {
            if (status.desc.equals(desc)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid booth status desc: " + desc);
    }
}