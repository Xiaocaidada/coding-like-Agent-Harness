package com.craftmarket.service.impl;

import com.craftmarket.entity.User;
import com.craftmarket.entity.Vendor;
import com.craftmarket.enums.UserRole;
import com.craftmarket.mapper.UserMapper;
import com.craftmarket.mapper.VendorMapper;
import com.craftmarket.service.AuthService;
import com.craftmarket.utils.JwtUtil;
import com.craftmarket.utils.Result;
import com.craftmarket.vo.JwtResponse;
import com.craftmarket.vo.UserVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 认证服务实现类
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private VendorMapper vendorMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public String login(String username, String password) {
        // 查找用户
        User user = userMapper.findByUsername(username);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }

        // 生成token
        return jwtUtil.generateToken(username, user.getRole().name(), user.getVendorId());
    }

    @Override
    @Transactional
    public boolean register(String username, String password, String phone, String name, String businessType) {
        // 检查用户名是否已存在
        if (userMapper.findByUsername(username) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已被摊主使用
        Vendor existingVendor = vendorMapper.findByPhone(phone);
        if (existingVendor != null) {
            throw new RuntimeException("手机号已被其他摊主使用");
        }

        // 创建摊主信息
        Vendor vendor = new Vendor();
        vendor.setName(name);
        vendor.setPhone(phone);
        vendor.setBusinessType(businessType);
        vendor.setStatus((byte) 1);
        vendor.setCreateTime(new Date());
        vendor.setUpdateTime(new Date());

        int vendorResult = vendorMapper.insert(vendor);
        if (vendorResult <= 0) {
            throw new RuntimeException("创建摊主信息失败");
        }

        // 创建用户信息
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(UserRole.VENDOR);
        user.setVendorId(vendor.getId());
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());

        return userMapper.insert(user) > 0;
    }

    @Override
    public String refreshToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("无效的token");
        }

        if (jwtUtil.isTokenExpired(token)) {
            throw new RuntimeException("token已过期");
        }

        return jwtUtil.refreshToken(token);
    }

    @Override
    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    @Override
    public User getUserFromToken(String token) {
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("无效的token");
        }

        String username = jwtUtil.getUsernameFromToken(token);
        return userMapper.findByUsername(username);
    }

    @Override
    public boolean logout(String token) {
        // JWT无状态，logout主要是客户端清除token
        // 这里可以添加token黑名单逻辑，如果需要的话
        return true;
    }
}