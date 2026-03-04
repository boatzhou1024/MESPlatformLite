package com.boatzhou.mes.controller;

import com.boatzhou.mes.common.Result;
import com.boatzhou.mes.dto.device.DeviceCreateRequest;
import com.boatzhou.mes.dto.device.DeviceUpdateRequest;
import com.boatzhou.mes.entity.Device;
import com.boatzhou.mes.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
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
 * 设备管理控制器。
 */
@RestController
@RequestMapping("/api/devices")
@Tag(name = "设备管理")
@SecurityRequirement(name = "BearerAuth")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    /**
     * 新增设备。
     */
    @PostMapping
    @Operation(summary = "新增设备")
    public Result<Device> create(@Valid @RequestBody DeviceCreateRequest request) {
        return Result.success(deviceService.createDevice(request));
    }

    /**
     * 根据 ID 更新设备。
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新设备")
    public Result<Device> update(@PathVariable Long id, @Valid @RequestBody DeviceUpdateRequest request) {
        return Result.success(deviceService.updateDevice(id, request));
    }

    /**
     * 删除设备。
     *
     * <p>该接口由安全配置限制为 ADMIN 角色可访问。</p>
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除设备", description = "仅 ADMIN 可访问")
    public Result<Void> delete(@PathVariable Long id) {
        deviceService.deleteDevice(id);
        return Result.success();
    }

    /**
     * 查询单个设备详情。
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询设备详情")
    public Result<Device> getById(@PathVariable Long id) {
        return Result.success(deviceService.getById(id));
    }

    /**
     * 查询设备列表（支持筛选）。
     */
    @GetMapping
    @Operation(summary = "查询设备列表", description = "可按状态/类型/关键字筛选")
    public Result<List<Device>> list(@RequestParam(required = false) String status,
                                     @RequestParam(required = false) String deviceType,
                                     @RequestParam(required = false) String keyword) {
        return Result.success(deviceService.list(status, deviceType, keyword));
    }
}
