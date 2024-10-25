package com.metaverse.logistics.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.constant.PermissionConstant;
import com.metaverse.common.model.Result;
import com.metaverse.common.permission.Permission;
import com.metaverse.logistics.req.FillAddressReq;
import com.metaverse.logistics.req.SetTrackingNumberReq;
import com.metaverse.logistics.resp.userPhysicalDistributionViewResp;
import com.metaverse.logistics.service.PhysicalDistributionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * <p>
 * 礼品寄送物流表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-10-23 15:01:16
 */
@RestController
@RequestMapping("/metaversePhysicalDistribution")
@RequiredArgsConstructor
@Validated
@Api("物流")
public class MetaversePhysicalDistributionController {

    private final PhysicalDistributionService physicalDistributionService;

    @PostMapping("/fillAddress")
    @ApiOperation(value = "补充收货信息", tags = "1.0.0")
    public Result<Boolean> fillAddress(@RequestBody @ApiParam(value = "收货信息", required = true) FillAddressReq req) {
        return Result.success(physicalDistributionService.fillAddress(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @PostMapping("/setTrackingNumber")
    @ApiOperation(value = "设置快递单号", tags = "1.0.0")// 运营人员才能访问的接口
    @Permission(resourceTypeElements = PermissionConstant.ResourceType.LOGISTICS_TRACKING_NUMBER, action = PermissionConstant.Action.UPDATE)
    public Result<Void> setTrackingNumber(@RequestBody @ApiParam(value = "快递单号", required = true) @Valid SetTrackingNumberReq req) {
        return Result.modify(physicalDistributionService.setTrackingNumber(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @GetMapping("/confirmReceipt")
    @ApiOperation(value = "当前用户确认收货", tags = "1.0.0")
    public Result<Void> confirmReceipt(@RequestParam(value = "id", required = false) @ApiParam(value = "物流ID", required = true) @NotNull(message = "物流ID不能为空") Long id) {
        return Result.modify(physicalDistributionService.confirmReceipt(MetaverseContextUtil.getCurrentUserId(), id));
    }

    @GetMapping("/userPhysicalDistributionView")
    @ApiOperation(value = "用户物流视图", tags = "1.0.0")
    public Result<List<userPhysicalDistributionViewResp>> userPhysicalDistributionView() {
        return Result.success(physicalDistributionService.userPhysicalDistributionView(MetaverseContextUtil.getCurrentUserId()));
    }
}
