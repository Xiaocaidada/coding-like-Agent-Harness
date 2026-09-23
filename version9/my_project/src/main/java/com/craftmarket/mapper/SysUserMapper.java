package com.craftmarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.craftmarket.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户Mapper接口
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     *
     * @param phone 手机号
     * @return 用户信息
     */
    SysUser selectByPhone(@Param("phone") String phone);

    /**
     * 根据用户名和手机号查询用户
     *
     * @param username 用户名
     * @param phone    手机号
     * @return 用户信息
     */
    SysUser selectByUsernameAndPhone(@Param("username") String username, @Param("phone") String phone);
}