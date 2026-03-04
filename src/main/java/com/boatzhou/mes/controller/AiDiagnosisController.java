package com.boatzhou.mes.controller;

import com.boatzhou.mes.common.Result;
import com.boatzhou.mes.dto.ai.AiDiagnosisRequest;
import com.boatzhou.mes.dto.ai.AiDiagnosisResponse;
import com.boatzhou.mes.service.AiDiagnosisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * AI 辅助诊断控制器。
 */
@RestController
@RequestMapping("/api/ai")
@Tag(name = "AI 辅助诊断")
@SecurityRequirement(name = "BearerAuth")
public class AiDiagnosisController {

    private final AiDiagnosisService aiDiagnosisService;

    public AiDiagnosisController(AiDiagnosisService aiDiagnosisService) {
        this.aiDiagnosisService = aiDiagnosisService;
    }

    /**
     * 设备故障诊断接口。
     *
     * <p>输入设备状态和异常描述，返回可能原因与处理建议。</p>
     */
    @PostMapping("/diagnosis")
    @Operation(summary = "设备故障诊断")
    public Result<AiDiagnosisResponse> diagnosis(@Valid @RequestBody AiDiagnosisRequest request) {
        return Result.success(aiDiagnosisService.diagnose(request));
    }
}
