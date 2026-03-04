package com.boatzhou.mes.dto.ai;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * AI 诊断响应体。
 */
@Data
public class AiDiagnosisResponse {

    /** 回显设备编码。 */
    private String deviceCode;

    /** 回显设备状态。 */
    private String deviceStatus;

    /** 可能故障原因列表。 */
    private List<String> possibleCauses;

    /** 处理建议。 */
    private String suggestion;

    /** 置信度（一般 0~1）。 */
    private BigDecimal confidence;

    /** 是否命中缓存。 */
    private Boolean fromCache;
}
