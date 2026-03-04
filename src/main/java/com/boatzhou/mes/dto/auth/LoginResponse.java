package com.boatzhou.mes.dto.auth;

import lombok.Data;

import java.util.List;

/**
 * 登录成功响应体。
 */
@Data
public class LoginResponse {

    /** JWT 令牌字符串。 */
    private String token;

    /** Token 类型，一般是 Bearer。 */
    private String tokenType;

    /** 过期时间（秒）。 */
    private Long expiresIn;

    /** 当前登录用户名。 */
    private String username;

    /** 用户昵称（可选）。 */
    private String nickname;

    /** 用户角色列表。 */
    private List<String> roles;
}
