package com.boatzhou.mes.controller;

import com.boatzhou.mes.common.Result;
import com.boatzhou.mes.service.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * User-role relation controller.
 */
@RestController
@RequestMapping("/api/users")
@Tag(name = "用户角色管理")
@SecurityRequirement(name = "BearerAuth")
public class UserRoleController {

    private final UserRoleService userRoleService;

    public UserRoleController(UserRoleService userRoleService) {
        this.userRoleService = userRoleService;
    }

    /**
     * Query role ids of a user.
     */
    @GetMapping("/{userId}/roles")
    @Operation(summary = "查询用户角色ID列表")
    public Result<List<Long>> listRoleIds(@PathVariable Long userId) {
        return Result.success(userRoleService.listRoleIdsByUserId(userId));
    }

    /**
     * Replace all roles of a user.
     */
    @PutMapping("/{userId}/roles")
    @Operation(summary = "覆盖设置用户角色")
    public Result<Void> replaceRoles(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        userRoleService.replaceUserRoles(userId, roleIds);
        return Result.success();
    }

    /**
     * Add one role to a user.
     */
    @PostMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "给用户添加角色")
    public Result<Void> addRole(@PathVariable Long userId, @PathVariable Long roleId) {
        userRoleService.addUserRole(userId, roleId);
        return Result.success();
    }

    /**
     * Remove one role from a user.
     */
    @DeleteMapping("/{userId}/roles/{roleId}")
    @Operation(summary = "移除用户角色")
    public Result<Void> removeRole(@PathVariable Long userId, @PathVariable Long roleId) {
        userRoleService.removeUserRole(userId, roleId);
        return Result.success();
    }
}
