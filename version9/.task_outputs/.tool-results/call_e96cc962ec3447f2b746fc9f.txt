package com.craftmarket.vo;

import lombok.Data;

/**
 * JWT响应VO
 */
@Data
public class JwtResponse {

    /**
     * JWT token
     */
    private String token;

    /**
     * token类型
     */
    private String type = "Bearer";

    /**
     * 用户信息
     */
    private UserVO user;
}