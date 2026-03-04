package com.boatzhou.mes.service;

import com.boatzhou.mes.dto.workorder.WorkOrderAssignRequest;
import com.boatzhou.mes.dto.workorder.WorkOrderCreateRequest;
import com.boatzhou.mes.dto.workorder.WorkOrderProgressRequest;
import com.boatzhou.mes.dto.workorder.WorkOrderStatusRequest;
import com.boatzhou.mes.entity.WorkOrder;

import java.util.List;

/**
 * 工单领域服务接口。
 */
public interface WorkOrderService {

    /**
     * 创建工单。
     */
    WorkOrder create(WorkOrderCreateRequest request);

    /**
     * 分配工单处理人。
     */
    WorkOrder assign(Long id, WorkOrderAssignRequest request);

    /**
     * 更新工单进度（带乐观锁版本号）。
     */
    WorkOrder updateProgress(Long id, WorkOrderProgressRequest request);

    /**
     * 更新工单状态（带乐观锁版本号）。
     */
    WorkOrder updateStatus(Long id, WorkOrderStatusRequest request);

    /**
     * 按 ID 查询工单。
     */
    WorkOrder getById(Long id);

    /**
     * 条件查询工单列表。
     */
    List<WorkOrder> list(String status, Long assigneeId, Long deviceId);
}
