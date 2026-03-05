package com.boatzhou.mes.service.impl;

import com.boatzhou.mes.common.BusinessException;
import com.boatzhou.mes.common.ErrorCode;
import com.boatzhou.mes.entity.SysRole;
import com.boatzhou.mes.entity.SysUser;
import com.boatzhou.mes.mapper.SysRoleMapper;
import com.boatzhou.mes.mapper.SysUserMapper;
import com.boatzhou.mes.mapper.SysUserRoleMapper;
import com.boatzhou.mes.service.UserRoleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * User-role relation business implementation.
 */
@Service
public class UserRoleServiceImpl implements UserRoleService {

    private final SysUserMapper sysUserMapper;
    private final SysRoleMapper sysRoleMapper;
    private final SysUserRoleMapper sysUserRoleMapper;

    public UserRoleServiceImpl(SysUserMapper sysUserMapper,
                               SysRoleMapper sysRoleMapper,
                               SysUserRoleMapper sysUserRoleMapper) {
        this.sysUserMapper = sysUserMapper;
        this.sysRoleMapper = sysRoleMapper;
        this.sysUserRoleMapper = sysUserRoleMapper;
    }

    @Override
    public List<Long> listRoleIdsByUserId(Long userId) {
        validateUserExists(userId);
        return sysUserRoleMapper.selectRoleIdsByUserId(userId);
    }

    @Override
    public List<String> listRoleCodesByUserId(Long userId) {
        validateUserExists(userId);
        return sysUserRoleMapper.selectRoleCodesByUserId(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replaceUserRoles(Long userId, List<Long> roleIds) {
        validateUserExists(userId);
        List<Long> normalizedRoleIds = normalizeRoleIds(roleIds);

        if (normalizedRoleIds.isEmpty()) {
            sysUserRoleMapper.deleteByUserId(userId);
            return;
        }

        validateAllRolesExist(normalizedRoleIds);
        sysUserRoleMapper.deleteByUserId(userId);
        sysUserRoleMapper.batchInsertIgnore(userId, normalizedRoleIds);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addUserRole(Long userId, Long roleId) {
        validateUserExists(userId);
        validateRoleExists(roleId);
        sysUserRoleMapper.insertIgnore(userId, roleId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeUserRole(Long userId, Long roleId) {
        validateUserExists(userId);
        if (roleId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "roleId is required");
        }
        sysUserRoleMapper.deleteByUserIdAndRoleId(userId, roleId);
    }

    private void validateUserExists(Long userId) {
        if (userId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "userId is required");
        }
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "user not found");
        }
    }

    private void validateRoleExists(Long roleId) {
        if (roleId == null) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "roleId is required");
        }
        SysRole role = sysRoleMapper.selectById(roleId);
        if (role == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "role not found");
        }
    }

    private void validateAllRolesExist(List<Long> roleIds) {
        List<SysRole> roles = sysRoleMapper.selectBatchIds(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "contains invalid roleId");
        }
    }

    private List<Long> normalizeRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return List.of();
        }
        LinkedHashSet<Long> deduplicated = new LinkedHashSet<>();
        for (Long roleId : roleIds) {
            if (roleId == null) {
                throw new BusinessException(ErrorCode.BAD_REQUEST, "roleId cannot be null");
            }
            deduplicated.add(roleId);
        }
        return new ArrayList<>(deduplicated);
    }
}
