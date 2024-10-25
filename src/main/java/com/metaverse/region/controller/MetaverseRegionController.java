package com.metaverse.region.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.constant.PermissionConstant;
import com.metaverse.common.model.Result;
import com.metaverse.common.permission.Permission;
import com.metaverse.region.req.ModifyRegionNameReq;
import com.metaverse.region.req.ModifyRegionServerLocationReq;
import com.metaverse.region.req.RegionCreateReq;
import com.metaverse.region.resp.MetaverseRegionResp;
import com.metaverse.region.service.MetaverseRegionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 区服 前端控制器 超级管理员 运营人员
 * </p>
 *
 * @author Hainui
 * @since 2024-09-03
 */
@RestController
@RequestMapping("/region")
@RequiredArgsConstructor
@Api("分区")
public class MetaverseRegionController {
    private final MetaverseRegionService regionService;

    @GetMapping("/getAllMetaverseRegion")
    @ApiOperation(value = "获取所有开放的区服", tags = "1.0.0")
    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.REGION}, action = PermissionConstant.Action.READ)
    public Result<List<MetaverseRegionResp>> getAllMetaverseRegion() {
        return Result.success(regionService.getAllMetaverseRegion());
    }

    @PostMapping("/create")
    @ApiOperation(value = "新建区服", tags = "1.0.0")
    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.REGION}, action = PermissionConstant.Action.CREATE)
    public Result<Long> create(@ApiParam(name = "新建区服请求参数", required = true) @RequestBody @Valid RegionCreateReq req) {
        return Result.success(regionService.create(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/modifyRegionName")
    @ApiOperation(value = "修改区服名", tags = "1.0.0")
    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.REGION}, action = PermissionConstant.Action.UPDATE)
    public Result<Void> modifyRegionName(@ApiParam(name = "修改区域名称请求参数", required = true) @RequestBody @Valid ModifyRegionNameReq req) {
        return Result.modify(regionService.modifyRegionName(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/modifyRegionLocationList")
    @ApiOperation(value = "修改区服地址", tags = "1.0.0")
    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.REGION}, action = PermissionConstant.Action.UPDATE)
    public Result<Void> modifyRegionLocationList(@ApiParam(name = "修改区域地址请求参数", required = true) @RequestBody @Valid ModifyRegionServerLocationReq req) {
        return Result.modify(regionService.modifyRegionLocationList(req, MetaverseContextUtil.getCurrentUserId()));
    }

}
