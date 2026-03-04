package com.boatzhou.mes.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 登录请求参数。
 */
@Data
public class LoginRequest {

    /** 登录用户名。 */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /** 登录密码（前端明文传入，后端进行校验）。 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
