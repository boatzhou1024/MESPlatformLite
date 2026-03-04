package com.boatzhou.mes.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 设备实体，对应表：devices。
 */
@Data
@TableName("devices")
public class Device {

    /** 主键 ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 设备编码（唯一）。 */
    private String deviceCode;

    /** 设备名称。 */
    private String deviceName;

    /** 设备类型。 */
    private String deviceType;

    /** 设备状态：ONLINE/OFFLINE/FAULT。 */
    private String status;

    /** 设备位置。 */
    private String location;

    /** 扩展描述。 */
    private String description;

    /** 最后心跳时间。 */
    private LocalDateTime lastHeartbeat;

    /** 创建时间（自动填充）。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间（自动填充）。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
