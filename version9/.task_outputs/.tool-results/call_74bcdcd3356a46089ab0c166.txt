package com.craftmarket.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.craftmarket.entity.SysUser;
import com.craftmarket.dto.LoginRequest;
import com.craftmarket.vo.JwtResponse;
import com.craftmarket.vo.UserVO;

/**
 * 系统用户服务接口
 */
public interface SysUserService extends IService<SysUser> {

    /**
     * 用户登录
     *
     * @param loginRequest 登录请求
     * @return JWT响应
     */
    JwtResponse login(LoginRequest loginRequest);

    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 注册成功返回用户信息
     */
    UserVO register(SysUser user);

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser getByUsername(String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    SysUser getByPhone(String phone);

    /**
     * 更新用户信息
     *
     * @param user 用户信息
     * @return 更新后的用户信息
     */
    UserVO updateUserInfo(SysUser user);

    /**
     * 修改密码
     *
     * @param userId      用户ID
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     */
    void changePassword(Long userId, String oldPassword, String newPassword);
}