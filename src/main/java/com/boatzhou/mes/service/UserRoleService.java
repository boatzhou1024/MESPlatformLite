package com.boatzhou.mes.service;

import java.util.List;

/**
 * User-role relation business service.
 */
public interface UserRoleService {

    /**
     * Query all role ids owned by a user.
     */
    List<Long> listRoleIdsByUserId(Long userId);

    /**
     * Query all role codes owned by a user.
     */
    List<String> listRoleCodesByUserId(Long userId);

    /**
     * Replace all roles of a user in one transaction.
     */
    void replaceUserRoles(Long userId, List<Long> roleIds);

    /**
     * Add one role to a user.
     */
    void addUserRole(Long userId, Long roleId);

    /**
     * Remove one role from a user.
     */
    void removeUserRole(Long userId, Long roleId);
}
