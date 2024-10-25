package com.metaverse.user.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.AddGroupReq;
import com.metaverse.user.req.GroupReq;
import com.metaverse.user.resp.MetaverseGroupRequestResp;
import com.metaverse.user.resp.UserGroupQuestionResp;
import com.metaverse.user.service.GroupJoinRequestService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <p>
 * 入群申请表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@RestController
@RequestMapping("/metaverseGroupJoinRequest")
@RequiredArgsConstructor
@Validated
@Api("入群申请")
public class MetaverseGroupJoinRequestController {
    private final GroupJoinRequestService groupJoinRequestService;

    @PostMapping("/joinGroupRequest")
    @ApiOperation(value = "加入群组请求", tags = "1.0.0")
    public Result<UserGroupQuestionResp> joinGroupRequest(@RequestBody @ApiParam(value = "加入群组请求参数", required = true) @Valid AddGroupReq req) {
        return Result.success(groupJoinRequestService.joinGroupRequest(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @PostMapping("/rejectGroupRequest")
    @ApiOperation(value = "管理层拒绝用户入群", tags = "1.0.0")
    public Result<Boolean> rejectGroupRequest(@RequestBody @ApiParam(value = "拒绝群组请求参数", required = true) @Valid GroupReq req) {
        return Result.success(groupJoinRequestService.rejectGroupRequest(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @PostMapping("/agreeGroupRequest")
    @ApiOperation(value = "管理层同意用户入群", tags = "1.0.0")
    public Result<Boolean> agreeGroupRequest(@RequestBody @ApiParam(value = "同意群组请求参数", required = true) @Valid GroupReq req) {
        return Result.success(groupJoinRequestService.agreeGroupRequest(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @GetMapping("/getUnagreedGroupRequestsOnTargetGroup")
    @ApiOperation(value = "获取所有对目标群的入群请求(未同意)-仅群管理层调用-用于及时提醒", tags = "1.0.0")
    public Result<List<MetaverseGroupRequestResp>> getUnagreedGroupRequestsOnTargetGroup(@RequestParam("groupId") @ApiParam(value = "群组ID", required = true) Long groupId) {
        return Result.success(groupJoinRequestService.getUnagreedGroupRequestsOnTargetGroup(groupId));
    }

    @GetMapping("/getGroupRequestsOnTargetGroup")
    @ApiOperation(value = "获取所有对目标群的入群请求(所有)-仅群管理层调用-用于查看历史请求详情", tags = "1.0.0")
    public Result<List<MetaverseGroupRequestResp>> getGroupRequestsOnTargetGroup(@RequestParam("groupId") @ApiParam(value = "群组ID", required = true) Long groupId) {
        return Result.success(groupJoinRequestService.getGroupRequestsOnTargetGroup(groupId));
    }
}




























