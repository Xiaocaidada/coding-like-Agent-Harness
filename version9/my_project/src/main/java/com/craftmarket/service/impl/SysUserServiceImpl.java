package com.craftmarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.craftmarket.entity.SysUser;
import com.craftmarket.mapper.SysUserMapper;
import com.craftmarket.service.SysUserService;
import com.craftmarket.utils.JwtUtil;
import com.craftmarket.vo.JwtResponse;
import com.craftmarket.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 系统用户服务实现类
 */
@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public SysUserServiceImpl(PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public JwtResponse login(LoginRequest loginRequest) {
        // 根据用户名查询用户
        SysUser user = getByUsername(loginRequest.getUsername());
        
        if (user == null || !passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        if (!"ACTIVE".equals(user.getStatus())) {
            throw new RuntimeException("账号已被禁用");
        }

        // 生成JWT token
        String token = jwtUtil.generateToken(user);
        
        // 构建响应
        JwtResponse response = new JwtResponse();
        response.setToken(token);
        response.setType("Bearer");
        
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        response.setUser(userVO);
        
        return response;
    }

    @Override
    @Transactional
    public UserVO register(SysUser user) {
        // 检查用户名是否已存在
        if (getByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }

        // 检查手机号是否已存在
        if (getByPhone(user.getPhone()) != null) {
            throw new RuntimeException("手机号已存在");
        }

        // 设置默认值
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("VENDOR"); // 默认为摊主角色
        user.setStatus("ACTIVE");
        user.setCreateTime(LocalDateTime.now());

        // 保存用户
        save(user);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    @Override
    public SysUser getByUsername(String username) {
        return getOne(new QueryWrapper<SysUser>().eq("username", username));
    }

    @Override
    public SysUser getByPhone(String phone) {
        return getOne(new QueryWrapper<SysUser>().eq("phone", phone));
    }

    @Override
    public UserVO updateUserInfo(SysUser user) {
        // 查询用户是否存在
        SysUser existingUser = getById(user.getId());
        if (existingUser == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新用户信息
        existingUser.setRealName(user.getRealName());
        existingUser.setPhone(user.getPhone());
        existingUser.setUpdateTime(LocalDateTime.now());

        updateById(existingUser);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(existingUser, userVO);
        return userVO;
    }

    @Override
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        // 查询用户
        SysUser user = getById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdateTime(LocalDateTime.now());
        updateById(user);
    }
}