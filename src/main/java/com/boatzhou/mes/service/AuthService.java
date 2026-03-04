package com.boatzhou.mes.service;

import com.boatzhou.mes.dto.auth.LoginRequest;
import com.boatzhou.mes.dto.auth.LoginResponse;

/**
 * 认证领域服务接口。
 */
public interface AuthService {

    /**
     * 校验账号并签发 JWT。
     */
    LoginResponse login(LoginRequest request);
}
