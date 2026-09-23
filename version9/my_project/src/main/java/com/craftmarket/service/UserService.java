package com.craftmarket.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.craftmarket.entity.User;

import java.util.List;

public interface UserService {

    // 保存用户
    boolean saveUser(User user);

    // 更新用户
    boolean updateUser(User user);

    // 根据ID查询用户
    User getUserById(Long id);

    // 根据用户名查询用户
    User getUserByUsername(String username);

    // 根据摊主ID查询用户
    User getUserByVendorId(Long vendorId);

    // 分页查询用户列表
    Page<User> getUsersPage(Integer current, Integer size, UserRole role);

    // 根据角色查询用户
    List<User> getUsersByRole(UserRole role);

    // 删除用户
    boolean deleteUser(Long id);

    // 根据用户名查询用户（含密码）
    User getUserByUsernameWithPassword(String username);

    // 检查用户名是否存在
    boolean usernameExists(String username);

    // 检查用户名是否已被其他用户使用
    boolean usernameExists(String username, Long excludeUserId);
}