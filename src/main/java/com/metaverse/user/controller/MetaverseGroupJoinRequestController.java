package com.metaverse.user.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.AddGroupReq;
import com.metaverse.user.req.AgreeGroupReq;
import com.metaverse.user.req.AnswerGroupQuestionReq;
import com.metaverse.user.resp.UserGroupQuestionResp;
import com.metaverse.user.service.GroupJoinRequestService;
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
public class MetaverseGroupJoinRequestController {

    private final GroupJoinRequestService groupJoinRequestService;

    @PostMapping("/joinGroupRequest")
    @ApiOperation(value = "加入群组请求", tags = "1.0.0")
    public Result<UserGroupQuestionResp> joinGroupRequest(@RequestBody @ApiParam(value = "加入群组请求参数", required = true) @Valid AddGroupReq req) {
        return Result.success(groupJoinRequestService.joinGroupRequest(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @PostMapping("/agreeGroupRequest")
    @ApiOperation(value = "管理层同意用户入群", tags = "1.0.0")
    public Result<Boolean> agreeGroupRequest(@RequestBody @ApiParam(value = "同意群组请求参数", required = true) @Valid AgreeGroupReq req) {
        return Result.success(groupJoinRequestService.agreeGroupRequest(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @PostMapping("/answerGroupQuestion")
    @ApiOperation(value = "回答问题", tags = "1.0.0")
    public Result<Boolean> answerGroupQuestion(@RequestBody @ApiParam(name = "回答群组问题请求参数", required = true) @Valid AnswerGroupQuestionReq req) {
        return Result.success(groupJoinRequestService.answerGroupQuestion(req, MetaverseContextUtil.getCurrentUserId()));
    }


}




























