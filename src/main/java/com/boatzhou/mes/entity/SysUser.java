package com.boatzhou.mes.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户实体，对应表：sys_users。
 */
@Data
@TableName("sys_users")
public class SysUser {

    /** 主键 ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 登录用户名（唯一）。 */
    private String username;

    /** 密码（建议存加密值）。 */
    private String password;

    /** 昵称。 */
    private String nickname;

    /** 状态：1 启用，0 禁用。 */
    private Integer status;

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
