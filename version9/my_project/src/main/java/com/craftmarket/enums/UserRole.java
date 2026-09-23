package com.craftmarket.enums;

/**
 * 用户角色枚举
 */
public enum UserRole {
    ADMIN("ADMIN", "管理员"),
    VENDOR("VENDOR", "摊主");

    private final String code;
    private final String desc;

    UserRole(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static UserRole fromCode(String code) {
        for (UserRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid user role code: " + code);
    }

    public static UserRole fromDesc(String desc) {
        for (UserRole role : values()) {
            if (role.desc.equals(desc)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Invalid user role desc: " + desc);
    }
}