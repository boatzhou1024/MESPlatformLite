package com.boatzhou.mes.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * AI 模块配置项（读取 application.yml 中 ai 前缀配置）。
 */
@Data
@Component
@ConfigurationProperties(prefix = "ai")
public class AiProperties {

    /** AI 服务 HTTP 地址。 */
    private String endpoint;

    /** 可选 API Key（会放入 Authorization 头）。 */
    private String apiKey;

    /** 是否启用本地 mock 结果（开发环境常用）。 */
    private boolean mockEnabled = true;

    /** 连接超时时间（毫秒）。 */
    private long connectTimeoutMs = 2000;

    /** 读取超时时间（毫秒）。 */
    private long readTimeoutMs = 5000;

    /** AI 诊断结果缓存时长（分钟）。 */
    private long cacheMinutes = 30;
}
