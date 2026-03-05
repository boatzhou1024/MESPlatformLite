package com.boatzhou.mes.service.impl;

import com.boatzhou.mes.common.BusinessException;
import com.boatzhou.mes.common.ErrorCode;
import com.boatzhou.mes.dto.auth.LoginRequest;
import com.boatzhou.mes.dto.auth.LoginResponse;
import com.boatzhou.mes.entity.SysUser;
import com.boatzhou.mes.mapper.SysUserMapper;
import com.boatzhou.mes.security.JwtProperties;
import com.boatzhou.mes.security.JwtTokenProvider;
import com.boatzhou.mes.service.AuthService;
import com.boatzhou.mes.service.UserRoleService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 认证服务实现。
 *
 * <p>流程：</p>
 * <p>1) 交给 Spring Security 完成账号密码认证；</p>
 * <p>2) 从数据库查询用户与角色；</p>
 * <p>3) 生成携带角色信息的 JWT；</p>
 * <p>4) 组装登录响应并返回。</p>
 */
@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final SysUserMapper sysUserMapper;
    private final UserRoleService userRoleService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public AuthServiceImpl(AuthenticationManager authenticationManager,
                           SysUserMapper sysUserMapper,
                           UserRoleService userRoleService,
                           JwtTokenProvider jwtTokenProvider,
                           JwtProperties jwtProperties) {
        this.authenticationManager = authenticationManager;
        this.sysUserMapper = sysUserMapper;
        this.userRoleService = userRoleService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    /**
     * 登录并签发 JWT。
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // 步骤1：统一走 Spring Security 认证链，校验用户名和密码。
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // 步骤2：认证通过后查询用户基础信息。
        SysUser user = sysUserMapper.selectByUsername(request.getUsername());
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED, "用户名或密码错误");
        }

        // 步骤3：查询角色列表，并规范 role 字符串。
        List<String> roles = userRoleService.listRoleCodesByUserId(user.getId())
                .stream()
                .map(roleCode -> roleCode.startsWith("ROLE_") ? roleCode.substring(5) : roleCode)
                .distinct()
                .toList();

        // 步骤4：生成签名 JWT。
        String token = jwtTokenProvider.generateToken(user.getUsername(), roles);

        // 步骤5：组装响应对象返回给前端。
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setTokenType(jwtProperties.getPrefix().trim());
        response.setExpiresIn(jwtTokenProvider.getExpireAt() / 1000);
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setRoles(roles);
        return response;
    }
}
