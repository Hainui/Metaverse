package com.metaverse.user.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.AnswerGroupQuestionReq;
import com.metaverse.user.service.GroupQuestionService;
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

}
