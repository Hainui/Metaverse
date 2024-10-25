package com.metaverse.user.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.UserFriendQuestionReq;
import com.metaverse.user.service.UserFriendQuestionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * <p>
 * 用户好友问题表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/metaverseUserFriendQuestion")
@Api("好友问题")
public class MetaverseUserFriendQuestionController {


    private final UserFriendQuestionService userFriendQuestionService;

    @PostMapping("/createQuestion")
    @ApiOperation(value = "新建问题", tags = "1.0.0")
    public Result<Boolean> createQuestion(@RequestBody @ApiParam(value = "创建问题请求参数", required = true) @Valid UserFriendQuestionReq req) {
        return Result.success(userFriendQuestionService.createQuestion(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @PostMapping("/modifyQuestion")
    @ApiOperation(value = "修改问题", tags = "1.0.0")
    public Result<Void> modifyQuestion(@RequestBody @ApiParam(value = "修改问题请求参数", required = true) @Valid UserFriendQuestionReq req) {
        return Result.modify(userFriendQuestionService.modifyQuestion(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @GetMapping("/disableQuestion")
    @ApiOperation(value = "禁用问题", tags = "1.0.0")
    public Result<Void> disableQuestion() {
        return Result.modify(userFriendQuestionService.disableQuestion(MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/enableQuestion")
    @ApiOperation(value = "启用问题", tags = "1.0.0")
    public Result<Void> enableQuestion() {
        return Result.modify(userFriendQuestionService.enableQuestion(MetaverseContextUtil.getCurrentUserId()));
    }
}



