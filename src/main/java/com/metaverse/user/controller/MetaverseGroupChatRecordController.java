package com.metaverse.user.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.GroupSendChatRecordReq;
import com.metaverse.user.service.UserGroupChatService;
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
 * 群聊记录表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/metaverseGroupChatRecord")
public class MetaverseGroupChatRecordController {

    private final UserGroupChatService userGroupChatService;


    @PostMapping("/sendChatMessages")
    @ApiOperation(value = "发送聊天信息(文本或图片)", tags = "1.0.0")
    public Result<Boolean> sendChatMessages(@RequestBody @Valid @ApiParam(name = "发送聊天信息参数", required = true) GroupSendChatRecordReq req) {
        return Result.success(userGroupChatService.sendChatMessages(req, MetaverseContextUtil.getCurrentUserId()));
    }
}
