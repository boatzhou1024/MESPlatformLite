package com.boatzhou.mes.dto.device;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 更新设备请求参数。
 *
 * <p>所有字段均为可选，传了才更新。</p>
 */
@Data
public class DeviceUpdateRequest {

    /** 新设备编码。 */
    private String deviceCode;

    /** 新设备名称。 */
    private String deviceName;

    /** 新设备类型。 */
    private String deviceType;

    /** 新设备状态。 */
    @Pattern(regexp = "ONLINE|OFFLINE|FAULT", message = "设备状态必须是 ONLINE/OFFLINE/FAULT")
    private String status;

    /** 新设备位置。 */
    private String location;

    /** 新描述信息。 */
    private String description;
}
