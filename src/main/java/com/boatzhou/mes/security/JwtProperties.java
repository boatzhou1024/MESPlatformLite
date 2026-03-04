package com.boatzhou.mes.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性（读取 application.yml 中 jwt 前缀）。
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /** 用于签名与验签的密钥。 */
    private String secret;

    /** Token 有效期（毫秒）。 */
    private long expiration;

    /** 存放 Token 的请求头名称（通常是 Authorization）。 */
    private String header;

    /** 请求头中的 Token 前缀（通常是 Bearer ）。 */
    private String prefix;
}
