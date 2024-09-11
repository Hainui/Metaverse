package com.metaverse.permission.controller;

import com.metaverse.common.Utils.JwtUtils;
import com.metaverse.common.model.Result;
import com.metaverse.permission.req.AuthoritiesForUserReq;
import com.metaverse.permission.req.AuthoritiesForUsersReq;
import com.metaverse.permission.req.UserAuthoritiesPageReq;
import com.metaverse.permission.resp.UserAuthoritiesPageResp;
import com.metaverse.permission.service.MetaverseUserPermissionRelationshipService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * <p>
 * 用户权限关联信息 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-09-09
 */
@RestController
@RequestMapping("/metaversePermissionRelationship")
@RequiredArgsConstructor
public class MetaverseUserPermissionRelationshipController {

    private final MetaverseUserPermissionRelationshipService permissionRelationshipService;

    @PostMapping("/authoritiesImpowerUsers")
    @ApiOperation(value = "多个权限同时授予多个用户", tags = "1.0.0")
    public Result<Boolean> authoritiesImpowerUsers(@ApiParam(name = "多个权限同时授予多个用户请求参数", required = true) @RequestBody @Valid AuthoritiesForUsersReq req) {
        // todo 权限校验
        return Result.success(permissionRelationshipService.authoritiesImpowerUsers(req));
    }

    @PostMapping("/authoritiesResetUsers")
    @ApiOperation(value = "多个权限同时重置多个用户", tags = "1.0.0")
    public Result<Boolean> authoritiesResetUsers(@ApiParam(name = "多个权限同时重置多个用户请求参数", required = true) @RequestBody @Valid AuthoritiesForUsersReq req) {
        // todo 权限校验 lzw
        return Result.success(permissionRelationshipService.authoritiesResetUsers(req, JwtUtils.getCurrentUserId()));
    }

    @PostMapping("/authoritiesRevokeUsers")
    @ApiOperation(value = "为多个用户批量删除选中的这些权限", tags = "1.0.0")
    public Result<Boolean> authoritiesRevokeUsers(@ApiParam(name = "为多个用户批量删除选中的这些权限请求参数", required = true) @RequestBody @Valid AuthoritiesForUsersReq req) {
        // todo 权限校验 lzw
        return Result.success(permissionRelationshipService.authoritiesRevokeForUsers(req, JwtUtils.getCurrentUserId()));
    }

    @PostMapping("/authoritiesRevokeForUser")
    @ApiOperation(value = "精准剔除单个用户一个或者多个权限", tags = "1.0.0")
    public Result<Boolean> authoritiesRevokeForUser(@ApiParam(name = "精准剔除单个用户的一个或者多个权限请求参数", required = true) @RequestBody @Valid AuthoritiesForUserReq req) {
        // todo 权限校验 lzw
        return Result.success(permissionRelationshipService.authoritiesRevokeForUser(req, JwtUtils.getCurrentUserId()));
    }

    @PostMapping("/authoritiesImpowerUser")
    @ApiOperation(value = "为单个用户添加一个或多个权限", tags = "1.0.0")
    public Result<Boolean> authoritiesImpowerUser(@ApiParam(name = "为单个用户添加一个或多个权限请求参数", required = true) @RequestBody @Valid AuthoritiesForUserReq req) {
        // todo 权限校验
        return Result.success(permissionRelationshipService.authoritiesImpowerUser(req));
    }

    @PostMapping("/userAuthoritiesPageView")
    @ApiOperation(value = "用户权限视图分页查询", tags = "1.0.0")
    public Result<UserAuthoritiesPageResp> userAuthoritiesPageView(@ApiParam(name = "用户权限视图分页查询请求参数", required = true) @RequestBody @Valid UserAuthoritiesPageReq req) {
        // todo 权限校验 lzw
        return Result.success(permissionRelationshipService.userAuthoritiesPageView(req));
    }
}
