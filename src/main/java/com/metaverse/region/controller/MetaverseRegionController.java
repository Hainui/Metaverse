package com.metaverse.region.controller;

import com.metaverse.common.Utils.JwtUtils;
import com.metaverse.common.model.Result;
import com.metaverse.region.req.ModifyRegionNameReq;
import com.metaverse.region.req.ModifyRegionServerLocationReq;
import com.metaverse.region.req.RegionCreateReq;
import com.metaverse.region.resp.MetaverseRegionResp;
import com.metaverse.region.service.MetaverseRegionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 区服 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-09-03
 */
@RestController
@RequestMapping("/region")
@RequiredArgsConstructor
public class MetaverseRegionController {
    private final MetaverseRegionService regionService;

    @GetMapping("/getAllMetaverseRegion")
    @ApiOperation(value = "获取所有开放的区服", tags = "1.0.0")
    public Result<List<MetaverseRegionResp>> getAllMetaverseRegion() {
        return Result.success(regionService.getAllMetaverseRegion());
    }

    @PostMapping("/create")
    @ApiOperation(value = "新建区服", tags = "1.0.0")
    public Result<Long> create(@ApiParam(name = "新建区服请求参数", required = true) @RequestBody @Valid RegionCreateReq req) {
        // todo 权限校验
        // {huoxing.shuixing}
        // huoxing.update.*
        // shuixing.update.*
        // *.update.*
        // shuixing.*.*
        // *.*.*
        return Result.success(regionService.create(req, JwtUtils.getCurrentUserId()));
    }

    @PostMapping("/modifyRegionName")
    @ApiOperation(value = "修改区服名", tags = "1.0.0")
    public Result<Boolean> modifyRegionName(@ApiParam(name = "修改区域名称请求参数", required = true) @RequestBody @Valid ModifyRegionNameReq req) {
        // todo 权限校验
        return Result.success(regionService.modifyRegionName(req, JwtUtils.getCurrentUserId()));
    }

    @PostMapping("/modifyRegionLocationList")
    @ApiOperation(value = "修改区服地址", tags = "1.0.0")
    public Result<Boolean> modifyRegionLocationList(@ApiParam(name = "修改区域地址请求参数", required = true) @RequestBody @Valid ModifyRegionServerLocationReq req) {
        // todo 权限校验
        return Result.success(regionService.modifyRegionLocationList(req, JwtUtils.getCurrentUserId()));
    }

}
