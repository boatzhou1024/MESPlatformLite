package com.boatzhou.mes.controller;

import com.boatzhou.mes.common.Result;
import com.boatzhou.mes.dto.workorder.WorkOrderAssignRequest;
import com.boatzhou.mes.dto.workorder.WorkOrderCreateRequest;
import com.boatzhou.mes.dto.workorder.WorkOrderProgressRequest;
import com.boatzhou.mes.dto.workorder.WorkOrderStatusRequest;
import com.boatzhou.mes.entity.WorkOrder;
import com.boatzhou.mes.service.WorkOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 工单管理控制器。
 */
@RestController
@RequestMapping("/api/work-orders")
@Tag(name = "工单管理")
@SecurityRequirement(name = "BearerAuth")
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    /**
     * 创建工单。
     */
    @PostMapping
    @Operation(summary = "创建工单")
    public Result<WorkOrder> create(@Valid @RequestBody WorkOrderCreateRequest request) {
        return Result.success(workOrderService.create(request));
    }

    /**
     * 分配工单给处理人。
     */
    @PutMapping("/{id}/assign")
    @Operation(summary = "分配工单", description = "请求体需携带 version 以支持乐观锁")
    public Result<WorkOrder> assign(@PathVariable Long id, @Valid @RequestBody WorkOrderAssignRequest request) {
        return Result.success(workOrderService.assign(id, request));
    }

    /**
     * 更新工单进度。
     */
    @PutMapping("/{id}/progress")
    @Operation(summary = "更新工单进度", description = "进度范围 0-100，且需携带 version")
    public Result<WorkOrder> updateProgress(@PathVariable Long id, @Valid @RequestBody WorkOrderProgressRequest request) {
        return Result.success(workOrderService.updateProgress(id, request));
    }

    /**
     * 更新工单状态。
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "更新工单状态", description = "状态值：PENDING/PROCESSING/COMPLETED")
    public Result<WorkOrder> updateStatus(@PathVariable Long id, @Valid @RequestBody WorkOrderStatusRequest request) {
        return Result.success(workOrderService.updateStatus(id, request));
    }

    /**
     * 根据 ID 查询工单详情。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询工单详情")
    public Result<WorkOrder> getById(@PathVariable Long id) {
        return Result.success(workOrderService.getById(id));
    }

    /**
     * 查询工单列表（支持条件筛选）。
     */
    @GetMapping
    @Operation(summary = "查询工单列表", description = "可按状态/处理人/设备筛选")
    public Result<List<WorkOrder>> list(@RequestParam(required = false) String status,
                                        @RequestParam(required = false) Long assigneeId,
                                        @RequestParam(required = false) Long deviceId) {
        return Result.success(workOrderService.list(status, assigneeId, deviceId));
    }
}
