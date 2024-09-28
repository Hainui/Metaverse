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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<Long> createUserGroup(@RequestBody @ApiParam(value = "新建群组请求参数", required = true) @Valid CreateUserGroupReq req) {
        return Result.success(userGroupService.createUserGroup(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @PostMapping("/modifyUserGroup")
    @ApiOperation(value = "修改群组信息", tags = "1.0.0")
    public Result<Void> modifyUserGroup(@RequestBody @ApiParam(value = "修改群组信息请求参数", required = true) @Valid ModifyUserGroupReq req) {
        return Result.modify(userGroupService.modifyUserGroup(MetaverseContextUtil.getCurrentUserId(), req));
    }
}
