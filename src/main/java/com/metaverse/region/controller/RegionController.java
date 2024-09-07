package com.metaverse.region.controller;

import com.metaverse.common.Utils.JwtUtils;
import com.metaverse.common.model.Result;
import com.metaverse.region.req.ModifyRegionNameReq;
import com.metaverse.region.req.ModifyRegionServerLocationReq;
import com.metaverse.region.req.RegionCreateReq;
import com.metaverse.region.resp.RegionResp;
import com.metaverse.region.service.RegionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
public class RegionController {
    private final RegionService regionService;

    @GetMapping("/getAllRegion")
    @ApiOperation(value = "获取所有开放的区服", tags = "1.0.0")
    public Result<List<RegionResp>> getAllRegion() {
        return Result.success(regionService.getAllRegion());
    }

    @PostMapping("/create")
    @ApiOperation(value = "新建区服", tags = "1.0.0")
    public Result<Long> create(@ApiParam(name = "新建区服请求参数", required = true) @RequestBody @Valid RegionCreateReq req) {
        // todo 权限校验
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
    public Result<Boolean> modifyRegionLocationList(@ApiParam(name = "修改区域地址请求参数", required = true) @RequestBody @NotEmpty(message = "地址集合不能为空") ModifyRegionServerLocationReq req) {
        // todo 权限校验
        return Result.success(regionService.modifyRegionLocationList(req, JwtUtils.getCurrentUserId()));
    }

}
