package com.boatzhou.mes.service;

import com.boatzhou.mes.dto.device.DeviceCreateRequest;
import com.boatzhou.mes.dto.device.DeviceUpdateRequest;
import com.boatzhou.mes.entity.Device;

import java.util.List;

/**
 * 设备领域服务接口。
 */
public interface DeviceService {

    /**
     * 创建设备。
     */
    Device createDevice(DeviceCreateRequest request);

    /**
     * 按 ID 更新设备。
     */
    Device updateDevice(Long id, DeviceUpdateRequest request);

    /**
     * 按 ID 删除设备。
     */
    void deleteDevice(Long id);

    /**
     * 按 ID 查询设备。
     */
    Device getById(Long id);

    /**
     * 条件查询设备列表。
     */
    List<Device> list(String status, String deviceType, String keyword);
}
