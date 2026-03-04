package com.boatzhou.mes.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.boatzhou.mes.entity.SysRole;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 角色 Mapper。
 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    /**
     * 根据用户 ID 查询其拥有的全部角色。
     */
    @Select("""
            select r.*
            from sys_roles r
            inner join sys_user_roles ur on ur.role_id = r.id
            where ur.user_id = #{userId}
            """)
    List<SysRole> selectByUserId(@Param("userId") Long userId);
}
