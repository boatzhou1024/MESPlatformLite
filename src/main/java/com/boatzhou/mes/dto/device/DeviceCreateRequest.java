package com.boatzhou.mes.dto.device;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 新建设备请求参数。
 */
@Data
public class DeviceCreateRequest {

    /** 设备业务编码（需唯一）。 */
    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    /** 设备名称。 */
    @NotBlank(message = "设备名称不能为空")
    private String deviceName;

    /** 设备类型（如 SENSOR / MOTOR）。 */
    @NotBlank(message = "设备类型不能为空")
    private String deviceType;

    /** 设备状态。 */
    @Pattern(regexp = "ONLINE|OFFLINE|FAULT", message = "设备状态必须是 ONLINE/OFFLINE/FAULT")
    private String status;

    /** 设备位置。 */
    private String location;

    /** 备注描述。 */
    private String description;
}
