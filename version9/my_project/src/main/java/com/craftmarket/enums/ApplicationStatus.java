package com.craftmarket.enums;

/**
 * 报名状态枚举
 */
public enum ApplicationStatus {
    PENDING("PENDING", "待审核"),
    APPROVED("APPROVED", "已通过"),
    REJECTED("REJECTED", "已驳回");

    private final String code;
    private final String desc;

    ApplicationStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static ApplicationStatus fromCode(String code) {
        for (ApplicationStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid application status code: " + code);
    }

    public static ApplicationStatus fromDesc(String desc) {
        for (ApplicationStatus status : values()) {
            if (status.desc.equals(desc)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid application status desc: " + desc);
    }
}