package com.boatzhou.mes.service;

import com.boatzhou.mes.dto.ai.AiDiagnosisRequest;
import com.boatzhou.mes.dto.ai.AiDiagnosisResponse;

/**
 * AI 诊断领域服务接口。
 */
public interface AiDiagnosisService {

    /**
     * 执行故障诊断（真实 AI 或本地 mock）。
     */
    AiDiagnosisResponse diagnose(AiDiagnosisRequest request);
}
