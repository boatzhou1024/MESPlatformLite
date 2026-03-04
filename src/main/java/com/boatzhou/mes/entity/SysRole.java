package com.boatzhou.mes.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色实体，对应表：sys_roles。
 */
@Data
@TableName("sys_roles")
public class SysRole {

    /** 主键 ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 角色编码（如 ADMIN）。 */
    private String roleCode;

    /** 角色名称。 */
    private String roleName;

    /** 角色描述。 */
    private String description;

    /** 创建时间。 */
    private LocalDateTime createdAt;
}
