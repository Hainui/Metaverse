package com.metaverse.user.domain.region.controller;

import com.metaverse.common.Utils.JwtUtils;
import com.metaverse.common.model.Result;
import com.metaverse.user.domain.region.req.ModifyRegionReq;
import com.metaverse.user.domain.region.req.RegionCreateReq;
import com.metaverse.user.domain.region.resp.RegionResp;
import com.metaverse.user.domain.region.service.RegionService;
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
        return Result.success(regionService.create(req, JwtUtils.getCurrentUserId()));
    }

    @PostMapping("/modifyRegionName")
    @ApiOperation(value = "修改区服名", tags = "1.0.0")
    public Result<Boolean> modifyRegionName(@ApiParam(name = "修改区域名称请求参数", required = true) @RequestBody @Valid ModifyRegionReq req) {
        return Result.success(regionService.modifyRegionName(req, JwtUtils.getCurrentUserId()));
    }

}
