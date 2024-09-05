package com.metaverse.region.controller;

import com.metaverse.common.Utils.JwtUtils;
import com.metaverse.common.model.Result;
import com.metaverse.region.req.RegionCreateReq;
import com.metaverse.region.req.RegionUpdateReq;
import com.metaverse.region.resp.RegionListResp;
import com.metaverse.region.service.RegionService;
import com.metaverse.user.req.ModifyUserNameReq;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 区服表 前端控制器
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
    public Result<List<RegionListResp>> getAllRegion() {
        return Result.success(regionService.getAllRegion());
    }


    @PostMapping("/create")//创建区服
    public Result<Long> create(@RequestBody @Valid RegionCreateReq req){
        return Result.success(regionService.create(req, JwtUtils.getCurrentUserId()));
    }

    @PutMapping("/updateRegionName")//修改区服
    public Result<Boolean> updateRegionName(@RequestBody @Valid RegionUpdateReq req) {
        return Result.success(regionService.updateRegionName(req, JwtUtils.getCurrentUserId()));
    }

}
