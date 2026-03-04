package com.boatzhou.mes.security;

import com.boatzhou.mes.entity.SysRole;
import com.boatzhou.mes.entity.SysUser;
import com.boatzhou.mes.mapper.SysRoleMapper;
import com.boatzhou.mes.mapper.SysUserMapper;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Security 用户加载器。
 *
 * <p>用于登录认证阶段根据用户名加载用户和权限。</p>
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;

    public CustomUserDetailsService(SysUserMapper sysUserMapper, SysRoleMapper sysRoleMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
    }

    /**
     * 根据用户名加载账号信息与角色权限。
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在");
        }

        if (user.getStatus() == null || user.getStatus() == 0) {
            throw new UsernameNotFoundException("用户已禁用");
        }

        List<SimpleGrantedAuthority> authorities = sysRoleMapper.selectByUserId(user.getId())
                .stream()
                .map(SysRole::getRoleCode)
                .map(roleCode -> roleCode.startsWith("ROLE_") ? roleCode : "ROLE_" + roleCode)
                .map(SimpleGrantedAuthority::new)
                .toList();

        return User.withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountLocked(false)
                .disabled(false)
                .build();
    }
}
