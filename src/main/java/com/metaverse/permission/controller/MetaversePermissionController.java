package com.metaverse.permission.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.constant.PermissionConstant;
import com.metaverse.common.model.Result;
import com.metaverse.common.permission.Permission;
import com.metaverse.permission.req.ModifyPermissionNameReq;
import com.metaverse.permission.req.ModifyPermissionReq;
import com.metaverse.permission.req.PermissionCreateReq;
import com.metaverse.permission.resp.MetaversePermissionResp;
import com.metaverse.permission.service.MetaversePermissionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 用户权限 前端控制器 超级管理员 运营人员
 * </p>
 *
 * @author Hainui
 * @since 2024-09-09
 */
@RestController
@RequestMapping("/metaversePermission")
@RequiredArgsConstructor
@Api("角色权限")
public class MetaversePermissionController {

    private final MetaversePermissionService permissionService;

    @GetMapping("/getAllMetaversePermission")
    @ApiOperation(value = "获取所有已定义的权限", tags = "1.0.0")
    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.PERMISSION}, action = PermissionConstant.Action.READ)
    public Result<List<MetaversePermissionResp>> getAllMetaversePermission() {
        return Result.success(permissionService.getAllMetaversePermission());
    }

    @PostMapping("/create")
    @ApiOperation(value = "定义新权限", tags = "1.0.0")
    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.PERMISSION}, action = PermissionConstant.Action.CREATE)
    public Result<Long> create(@ApiParam(name = "新权限请求参数", required = true) @RequestBody @Valid PermissionCreateReq req) {
        return Result.success(permissionService.create(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/modifyPermissionName")
    @ApiOperation(value = "修改权限名称", tags = "1.0.0")
    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.PERMISSION}, action = PermissionConstant.Action.UPDATE)
    public Result<Void> modifyPermissionName(@ApiParam(name = "修改权限名称请求参数", required = true) @RequestBody @Valid ModifyPermissionNameReq req) {
        return Result.modify(permissionService.modifyPermissionName(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/modifyPermissions")
    @ApiOperation(value = "修改权限的权限串集合", tags = "1.0.0")
    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.PERMISSION}, action = PermissionConstant.Action.UPDATE)
    public Result<Void> modifyPermissions(@ApiParam(name = "修改权限串请求参数", required = true) @RequestBody @Valid ModifyPermissionReq req) {
        return Result.modify(permissionService.modifyPermissions(req, MetaverseContextUtil.getCurrentUserId()));
    }

}
