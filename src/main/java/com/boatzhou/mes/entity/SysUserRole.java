package com.boatzhou.mes.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户-角色关联实体，对应表：sys_user_roles。
 */
@Data
@TableName("sys_user_roles")
public class SysUserRole {

    /** 用户 ID。 */
    private Long userId;

    /** 角色 ID。 */
    private Long roleId;

    /** 关联创建时间。 */
    private LocalDateTime createdAt;
}
