package com.boatzhou.mes.controller;

import com.boatzhou.mes.common.Result;
import com.boatzhou.mes.dto.auth.LoginRequest;
import com.boatzhou.mes.dto.auth.LoginResponse;
import com.boatzhou.mes.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口控制器。
 */
@RestController
@RequestMapping("/api/auth")
@Tag(name = "认证管理")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 用户登录接口。
     *
     * <p>校验用户名密码，通过后返回 JWT 令牌。</p>
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "校验账号密码并返回包含角色信息的 JWT Token")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.success(authService.login(request));
    }
}
