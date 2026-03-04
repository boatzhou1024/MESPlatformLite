package com.boatzhou.mes.service.impl;

import com.boatzhou.mes.common.BusinessException;
import com.boatzhou.mes.common.ErrorCode;
import com.boatzhou.mes.config.AiProperties;
import com.boatzhou.mes.dto.ai.AiDiagnosisRequest;
import com.boatzhou.mes.dto.ai.AiDiagnosisResponse;
import com.boatzhou.mes.service.AiDiagnosisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AI 诊断服务实现。
 *
 * <p>执行策略：</p>
 * <p>1) 先查 Redis 缓存；</p>
 * <p>2) 缓存未命中则调用 mock 或外部 AI 服务；</p>
 * <p>3) 将结果回写缓存，避免频繁调用 AI。</p>
 */
@Service
public class AiDiagnosisServiceImpl implements AiDiagnosisService {

    /** 诊断缓存 key 前缀。 */
    private static final String AI_DIAGNOSIS_CACHE_PREFIX = "mes:ai:diagnosis:";

    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final AiProperties aiProperties;

    public AiDiagnosisServiceImpl(StringRedisTemplate stringRedisTemplate,
                                  ObjectMapper objectMapper,
                                  AiProperties aiProperties) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.objectMapper = objectMapper;
        this.aiProperties = aiProperties;
    }

    /**
     * 执行诊断主流程。
     */
    @Override
    public AiDiagnosisResponse diagnose(AiDiagnosisRequest request) {
        String source = request.getDeviceCode() + "|" + request.getDeviceStatus() + "|" + request.getSymptom();
        String cacheKey = AI_DIAGNOSIS_CACHE_PREFIX
                + DigestUtils.md5DigestAsHex(source.getBytes(StandardCharsets.UTF_8));

        // 步骤1：先查缓存。
        String cachedJson = stringRedisTemplate.opsForValue().get(cacheKey);
        if (StringUtils.hasText(cachedJson)) {
            try {
                AiDiagnosisResponse response = objectMapper.readValue(cachedJson, AiDiagnosisResponse.class);
                response.setFromCache(true);
                return response;
            } catch (Exception ignored) {
                // 缓存解析失败时，直接回源。
            }
        }

        // 步骤2：根据配置选择 mock 或外部 AI。
        AiDiagnosisResponse response = aiProperties.isMockEnabled()
                ? buildMockResponse(request)
                : callExternalAiService(request);

        response.setFromCache(false);

        // 步骤3：回写缓存。
        try {
            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    objectMapper.writeValueAsString(response),
                    Duration.ofMinutes(aiProperties.getCacheMinutes())
            );
        } catch (Exception ignored) {
            // no-op
        }

        return response;
    }

    /**
     * 调用外部 AI HTTP 接口并映射结果。
     */
    private AiDiagnosisResponse callExternalAiService(AiDiagnosisRequest request) {
        if (!StringUtils.hasText(aiProperties.getEndpoint())) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 服务地址未配置");
        }

        try {
            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofMillis(aiProperties.getConnectTimeoutMs()))
                    .build();

            Map<String, Object> payload = Map.of(
                    "deviceCode", request.getDeviceCode(),
                    "deviceStatus", request.getDeviceStatus(),
                    "symptom", request.getSymptom()
            );

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(aiProperties.getEndpoint()))
                    .timeout(Duration.ofMillis(aiProperties.getReadTimeoutMs()))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)));

            if (StringUtils.hasText(aiProperties.getApiKey())) {
                requestBuilder.header("Authorization", "Bearer " + aiProperties.getApiKey());
            }

            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new BusinessException(ErrorCode.INTERNAL_ERROR,
                        "AI 服务调用失败，HTTP 状态码: " + response.statusCode());
            }

            JsonNode jsonNode = objectMapper.readTree(response.body());

            AiDiagnosisResponse diagnosisResponse = new AiDiagnosisResponse();
            diagnosisResponse.setDeviceCode(request.getDeviceCode());
            diagnosisResponse.setDeviceStatus(request.getDeviceStatus());
            diagnosisResponse.setPossibleCauses(parsePossibleCauses(jsonNode));
            diagnosisResponse.setSuggestion(parseSuggestion(jsonNode));
            diagnosisResponse.setConfidence(parseConfidence(jsonNode));
            return diagnosisResponse;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INTERNAL_ERROR, "AI 服务调用异常: " + e.getMessage());
        }
    }

    /**
     * 本地 mock 诊断结果（开发/演示环境使用）。
     */
    private AiDiagnosisResponse buildMockResponse(AiDiagnosisRequest request) {
        AiDiagnosisResponse response = new AiDiagnosisResponse();
        response.setDeviceCode(request.getDeviceCode());
        response.setDeviceStatus(request.getDeviceStatus());

        if ("FAULT".equalsIgnoreCase(request.getDeviceStatus())) {
            response.setPossibleCauses(List.of("传感器漂移", "电机过载", "供电电压不稳定"));
            response.setSuggestion("建议先重启并校准传感器，再检查电机负载与供电线路。");
            response.setConfidence(new BigDecimal("0.83"));
        } else {
            response.setPossibleCauses(List.of("网络抖动", "心跳上报间隔异常"));
            response.setSuggestion("建议检查网络连通性并核对心跳上报策略。");
            response.setConfidence(new BigDecimal("0.72"));
        }

        return response;
    }

    /**
     * 解析 possibleCauses 字段。
     */
    private List<String> parsePossibleCauses(JsonNode jsonNode) {
        JsonNode node = jsonNode.path("possibleCauses");
        if (!node.isArray()) {
            return List.of("暂无明确原因");
        }

        List<String> causes = new ArrayList<>();
        node.forEach(item -> causes.add(item.asText("")));
        return causes.isEmpty() ? List.of("暂无明确原因") : causes;
    }

    /**
     * 解析 suggestion 字段，缺失时给默认建议。
     */
    private String parseSuggestion(JsonNode jsonNode) {
        String suggestion = jsonNode.path("suggestion").asText();
        return StringUtils.hasText(suggestion)
                ? suggestion
                : "建议安排现场巡检，并结合历史工单进一步确认。";
    }

    /**
     * 解析 confidence 字段，缺失时使用默认值。
     */
    private BigDecimal parseConfidence(JsonNode jsonNode) {
        JsonNode confidenceNode = jsonNode.path("confidence");
        if (confidenceNode.isMissingNode() || confidenceNode.isNull()) {
            return new BigDecimal("0.60");
        }
        return BigDecimal.valueOf(confidenceNode.asDouble(0.60));
    }
}
