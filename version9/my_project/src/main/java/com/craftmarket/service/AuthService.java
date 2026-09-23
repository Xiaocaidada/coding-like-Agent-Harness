package com.craftmarket.service;

import com.craftmarket.entity.User;

public interface AuthService {

    // 用户登录
    String login(String username, String password);

    // 用户注册（摊主）
    boolean register(String username, String password, String phone, String name, String businessType);

    // 刷新token
    String refreshToken(String token);

    // 验证token
    boolean validateToken(String token);

    // 根据token获取用户信息
    User getUserFromToken(String token);

    // 注销登录
    boolean logout(String token);
}