package com.boatzhou.mes.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * Mapper for user-role relation table.
 *
 * <p>The table {@code sys_user_roles} uses a composite primary key
 * ({@code user_id + role_id}), so this mapper should use custom SQL methods
 * instead of extending MyBatis-Plus {@code BaseMapper} (which expects a single
 * {@code @TableId}).</p>
 */
public interface SysUserRoleMapper {

    /**
     * Query all role ids owned by a user.
     */
    @Select("""
            select role_id
            from sys_user_roles
            where user_id = #{userId}
            order by role_id
            """)
    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);

    /**
     * Query all role codes owned by a user.
     */
    @Select("""
            select r.role_code
            from sys_roles r
            inner join sys_user_roles ur on ur.role_id = r.id
            where ur.user_id = #{userId}
            order by r.id
            """)
    List<String> selectRoleCodesByUserId(@Param("userId") Long userId);

    /**
     * Add one user-role relation. If it already exists, keep it unchanged.
     */
    @Insert("""
            insert into sys_user_roles (user_id, role_id, created_at)
            values (#{userId}, #{roleId}, now())
            on duplicate key update role_id = values(role_id)
            """)
    int insertIgnore(@Param("userId") Long userId, @Param("roleId") Long roleId);

    /**
     * Batch add user-role relations. Existing relations are ignored.
     */
    @Insert({
            "<script>",
            "insert into sys_user_roles (user_id, role_id, created_at) values",
            "<foreach collection='roleIds' item='roleId' separator=','>",
            "(#{userId}, #{roleId}, now())",
            "</foreach>",
            "on duplicate key update role_id = values(role_id)",
            "</script>"
    })
    int batchInsertIgnore(@Param("userId") Long userId, @Param("roleIds") List<Long> roleIds);

    /**
     * Delete all role relations of a user.
     */
    @Delete("delete from sys_user_roles where user_id = #{userId}")
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * Delete one user-role relation.
     */
    @Delete("delete from sys_user_roles where user_id = #{userId} and role_id = #{roleId}")
    int deleteByUserIdAndRoleId(@Param("userId") Long userId, @Param("roleId") Long roleId);
}
