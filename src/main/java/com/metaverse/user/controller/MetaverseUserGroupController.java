package com.metaverse.user.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.CreateUserGroupReq;
import com.metaverse.user.req.ModifyUserGroupReq;
import com.metaverse.user.service.UserGroupService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 用户群组表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@RestController
@RequestMapping("/metaverseUserGroup")
@RequiredArgsConstructor
@Validated
public class MetaverseUserGroupController {

    private final UserGroupService userGroupService;

    @PostMapping("/createUserGroup")
    @ApiOperation(value = "新建群组", tags = "1.0.0")
    public Result<Long> createQuestion(@RequestBody @ApiParam(value = "新建群组请求参数", required = true) @Valid CreateUserGroupReq req) {
        return Result.success(userGroupService.createUserGroup(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @PostMapping("/modifyUserGroup")
    @ApiOperation(value = "修改群组信息", tags = "1.0.0")
    public Result<Void> modifyUserGroup(@RequestBody @ApiParam(value = "修改群组信息请求参数", required = true) @Valid ModifyUserGroupReq req) {
        return Result.modify(userGroupService.modifyUserGroup(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @GetMapping("/currentUserIsTargetGroupManagement")
    @ApiOperation(value = "判断当前用户是否是目标群的管理层(管理员或者群主)", tags = "1.0.0")
    public Result<Boolean> currentUserIsTargetGroupManagement(@RequestParam("groupId") @ApiParam(value = "群组ID", required = true) Long groupId) {
        return Result.success(userGroupService.memberUserIsManagement(MetaverseContextUtil.getCurrentUserId(), groupId));
    }

    @GetMapping("/currentUserIsTargetGroupOwner")
    @ApiOperation(value = "判断当前用户是否是目标群的群主", tags = "1.0.0")
    public Result<Boolean> currentUserIsTargetGroupOwner(@RequestParam("groupId") @ApiParam(value = "群组ID", required = true) Long groupId) {
        return Result.success(userGroupService.currentUserIsTargetGroupOwner(MetaverseContextUtil.getCurrentUserId(), groupId));
    }
}
