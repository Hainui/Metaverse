package com.metaverse.user.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.AnswerGroupQuestionReq;
import com.metaverse.user.req.GroupQuestionReq;
import com.metaverse.user.service.GroupQuestionService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * <p>
 * 群组问题表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@RestController
@RequestMapping("/metaverseGroupQuestion")
@RequiredArgsConstructor
@Validated
public class MetaverseGroupQuestionController {
    private final GroupQuestionService groupQuestionService;

    @PostMapping("/answerGroupQuestion")
    @ApiOperation(value = "回答问题", tags = "1.0.0")
    public Result<Boolean> answerGroupQuestion(@RequestBody @ApiParam(name = "回答群组问题请求参数", required = true) @Valid AnswerGroupQuestionReq req) {
        return Result.success(groupQuestionService.answerGroupQuestion(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/createGroupQuestion")
    @ApiOperation(value = "新建问题", tags = "1.0.0")
    public Result<Boolean> createGroupQuestion(@RequestBody @ApiParam(value = "创建问题请求参数", required = true) @Valid GroupQuestionReq req) {
        return Result.success(groupQuestionService.createGroupQuestion(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @PostMapping("/modifyGroupQuestion")
    @ApiOperation(value = "修改群组问题", tags = "1.0.0")
    public Result<Void> modifyGroupQuestion(@RequestBody @ApiParam(value = "修改问题请求参数", required = true) @Valid GroupQuestionReq req) {
        return Result.modify(groupQuestionService.modifyGroupQuestion(MetaverseContextUtil.getCurrentUserId(), req));
    }

    @GetMapping("/disableGroupQuestion")
    @ApiOperation(value = "禁用问题", tags = "1.0.0")
    public Result<Void> disableGroupQuestion(@RequestParam(value = "groupId", required = false) @ApiParam(name = "群组ID", required = true) @NotNull(message = "群组ID不能为空") Long groupId) {
        return Result.modify(groupQuestionService.disableGroupQuestion(MetaverseContextUtil.getCurrentUserId(), groupId));
    }

    @GetMapping("/enableGroupQuestion")
    @ApiOperation(value = "启用问题", tags = "1.0.0")
    public Result<Void> enableGroupQuestion(@RequestParam(value = "groupId", required = false) @ApiParam(name = "群组ID", required = true) @NotNull(message = "群组ID不能为空") Long groupId) {
        return Result.modify(groupQuestionService.enableGroupQuestion(MetaverseContextUtil.getCurrentUserId(), groupId));
    }
}
