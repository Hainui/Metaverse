package com.metaverse.permission.controller;

import com.metaverse.common.model.Result;
import com.metaverse.permission.req.AuthoritiesAccreditUsersReq;
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
    public Result<Boolean> authoritiesImpowerUsers(@ApiParam(name = "多个权限同时授予多个用户请求参数", required = true) @RequestBody @Valid AuthoritiesAccreditUsersReq req) {
        // todo 权限校验
        return Result.success(permissionRelationshipService.authoritiesImpowerUsers(req));
    }

    @PostMapping("/authoritiesResetUsers")
    @ApiOperation(value = "多个权限同时重置多个用户", tags = "1.0.0")
    public Result<Boolean> authoritiesResetUsers(@ApiParam(name = "多个权限同时重置多个用户请求参数", required = true) @RequestBody @Valid AuthoritiesAccreditUsersReq req) {
        // todo 权限校验
        return Result.success(permissionRelationshipService.authoritiesResetUsers(req));
    }
    // todo 为多个用户批量删除选中的这些权限
    // todo 精准剔除单个用户权限
    // todo 为单个用户精准添加权限
    // todo 用户权限视图查询


//    @PostMapping("/create")
//    @ApiOperation(value = "定义新权限", tags = "1.0.0")
//    public Result<Long> create(@ApiParam(name = "新权限请求参数", required = true) @RequestBody @Valid PermissionCreateReq req) {
//        // todo 权限校验
//        return Result.success(permissionRelationshipService.create(req, JwtUtils.getCurrentUserId()));
//    }
//
//    @PostMapping("/modifyPermissionName")
//    @ApiOperation(value = "修改权限名称", tags = "1.0.0")
//    public Result<Boolean> modifyPermissionName(@ApiParam(name = "修改权限名称请求参数", required = true) @RequestBody @Valid ModifyPermissionNameReq req) {
//        // todo 权限校验
//        return Result.success(permissionRelationshipService.modifyPermissionName(req, JwtUtils.getCurrentUserId()));
//    }
//
//    @PostMapping("/modifyPermissions")
//    @ApiOperation(value = "修改权限的权限串集合", tags = "1.0.0")
//    public Result<Boolean> modifyPermissions(@ApiParam(name = "修改权限串请求参数", required = true) @RequestBody @Valid ModifyPermissionReq req) {
//        // todo 权限校验
//        return Result.success(permissionRelationshipService.modifyPermissions(req, JwtUtils.getCurrentUserId()));
//    }

}
