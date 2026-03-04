package com.boatzhou.mes.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * AI 诊断请求参数。
 */
@Data
public class AiDiagnosisRequest {

    /** 设备编码。 */
    @NotBlank(message = "设备编码不能为空")
    private String deviceCode;

    /** 当前设备状态（ONLINE/OFFLINE/FAULT）。 */
    @NotBlank(message = "设备状态不能为空")
    private String deviceStatus;

    /** 运维人员输入的异常描述。 */
    @NotBlank(message = "异常描述不能为空")
    private String symptom;
}
