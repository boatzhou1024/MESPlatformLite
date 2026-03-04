package com.boatzhou.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boatzhou.mes.entity.SysUser;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户 Mapper。
 */
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 按用户名查询用户（登录认证使用）。
     */
    @Select("select * from sys_users where username = #{username} limit 1")
    SysUser selectByUsername(@Param("username") String username);
}
