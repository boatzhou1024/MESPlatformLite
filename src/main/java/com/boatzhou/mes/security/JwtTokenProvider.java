package com.boatzhou.mes.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * JWT 工具组件：负责签发、解析、校验 Token。
 */
@Component
public class JwtTokenProvider {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtTokenProvider(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 生成签名后的 JWT。
     *
     * @param username 用户名（放在 subject）
     * @param roles    角色集合（放在 roles claim）
     */
    public String generateToken(String username, List<String> roles) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + jwtProperties.getExpiration());

        return Jwts.builder()
                .subject(username)
                .claim("roles", CollectionUtils.isEmpty(roles) ? Collections.emptyList() : roles)
                .issuedAt(now)
                .expiration(expireAt)
                .signWith(secretKey)
                .compact();
    }

    /**
     * 校验 Token 是否有效（签名、格式、过期时间）。
     */
    public boolean validateToken(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 从 Token 中提取用户名（subject）。
     */
    public String getUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /**
     * 从 Token 的 roles claim 中提取角色列表。
     */
    public List<String> getRoles(String token) {
        Object roles = parseClaims(token).get("roles");
        if (roles instanceof List<?> roleList) {
            return roleList.stream().filter(Objects::nonNull).map(Object::toString).toList();
        }
        return Collections.emptyList();
    }

    /**
     * 返回 Token 有效期配置（毫秒）。
     */
    public long getExpireAt() {
        return jwtProperties.getExpiration();
    }

    /**
     * 解析并验签，返回 Claims。
     */
    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
