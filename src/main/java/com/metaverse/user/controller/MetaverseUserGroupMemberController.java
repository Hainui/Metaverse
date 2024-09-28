package com.metaverse.user.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.GrantAdministratorReq;
import com.metaverse.user.req.InviteUserJoinGroupReq;
import com.metaverse.user.resp.UserGroupResp;
import com.metaverse.user.service.UserGroupMemberService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 用户群组成员表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@RestController
@RequestMapping("/metaverseUserGroupMember")
@RequiredArgsConstructor
@Validated
public class MetaverseUserGroupMemberController {

    private final UserGroupMemberService userGroupMemberService;

    @GetMapping("/getTargetGroupAllUsers")
    @ApiOperation(value = "获取当前群聊的全部用户", tags = "1.0.0")
    public Result<UserGroupResp> getTargetGroupAllUsers(@RequestParam("groupId") @ApiParam(value = "群组ID", required = true) Long groupId) {
        return Result.success(userGroupMemberService.getTargetGroupAllUsers(groupId));
    }

    @PostMapping("/inviteUserJoinGroup")
    @ApiOperation(value = "管理层邀请用户入群", tags = "1.0.0")
    public Result<Boolean> inviteUserJoinGroup(@RequestBody @ApiParam(value = "同意群组请求参数", required = true) @Valid InviteUserJoinGroupReq req) {
        return Result.success(userGroupMemberService.inviteUserJoinGroup(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @PostMapping("/grantAdministrator")
    @ApiOperation(value = "赋予群成员为管理员", tags = "1.0.0")
    public Result<Void> grantAdministrator(@RequestBody @ApiParam(value = "赋予群成员为管理员请求参数", required = true) @Valid GrantAdministratorReq req) {
        return Result.modify(userGroupMemberService.grantAdministrator(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @GetMapping("/currentUserIsTargetGroupManagement")
    @ApiOperation(value = "判断当前用户是否是目标群的管理层(管理员或者群主)", tags = "1.0.0")
    public Result<Boolean> currentUserIsTargetGroupManagement(@RequestParam("groupId") @ApiParam(value = "群组ID", required = true) Long groupId) {
        return Result.success(userGroupMemberService.memberUserIsManagement(MetaverseContextUtil.getCurrentUserId(), groupId));
    }

    @GetMapping("/currentUserIsTargetGroupOwner")
    @ApiOperation(value = "判断当前用户是否是目标群的群主", tags = "1.0.0")
    public Result<Boolean> currentUserIsTargetGroupOwner(@RequestParam("groupId") @ApiParam(value = "群组ID", required = true) Long groupId) {
        return Result.success(userGroupMemberService.currentUserIsTargetGroupOwner(MetaverseContextUtil.getCurrentUserId(), groupId));
    }

}
