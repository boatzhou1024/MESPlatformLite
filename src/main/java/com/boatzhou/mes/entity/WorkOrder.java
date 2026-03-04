package com.boatzhou.mes.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.Version;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单实体，对应表：work_orders。
 */
@Data
@TableName("work_orders")
public class WorkOrder {

    /** 主键 ID。 */
    @TableId(type = IdType.AUTO)
    private Long id;

    /** 工单编号。 */
    private String orderNo;

    /** 工单标题。 */
    private String title;

    /** 问题描述。 */
    private String description;

    /** 关联设备 ID。 */
    private Long deviceId;

    /** 处理人 ID。 */
    private Long assigneeId;

    /** 优先级（数值越小优先级越高）。 */
    private Integer priority;

    /** 工单状态：PENDING/PROCESSING/COMPLETED。 */
    private String status;

    /** 进度百分比 [0,100]。 */
    private Integer progress;

    /** 乐观锁版本号。 */
    @Version
    private Integer version;

    /** 完成时间（状态为 COMPLETED 时赋值）。 */
    private LocalDateTime completedAt;

    /** 创建时间。 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdAt;

    /** 更新时间。 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedAt;
}
