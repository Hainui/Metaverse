package com.metaverse.user.controller;


import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.SendChatAudioReq;
import com.metaverse.user.req.SendChatRecordReq;
import com.metaverse.user.req.WithdrawChatMessageReq;
import com.metaverse.user.resp.UserFriendChatMesagesResp;
import com.metaverse.user.service.UserFriendChatService;
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
 * 用户好友聊天记录表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@RestController
@RequestMapping("/metaverseUserFriendChatRecord")
@RequiredArgsConstructor
@Validated
public class MetaverseUserFriendChatRecordController {

    private final UserFriendChatService userFriendChatService;

    @PostMapping("/sendChatMessages")
    @ApiOperation(value = "发送聊天信息(文本或图片)", tags = "1.0.0")
    public Result<Boolean> sendChatMessages(@RequestBody @Valid @ApiParam(name = "发送聊天信息参数", required = true) SendChatRecordReq req) {
        return Result.success(userFriendChatService.sendChatMessages(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/sendChatAudio")
    @ApiOperation(value = "发送聊天音频", tags = "1.0.0")
    public Result<Boolean> sendChatAudio(@RequestBody @Valid @ApiParam(name = "发送音频请求参数", required = true) SendChatAudioReq req) {
        return Result.success(userFriendChatService.sendChatAudio(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/sendChatFile")
    @ApiOperation(value = "发送聊天文件", tags = "1.0.0")
    public Result<Boolean> sendChatFile(@RequestParam("receiverId") @ApiParam(name = "接收信息的用户ID", required = true) @NotNull(message = "接收信息的用户ID不能为空") Long receiverId,
                                        @RequestParam("file") @ApiParam(name = "信息文件Id", required = true) @NotNull(message = "信息文件Id不能为空") Long fileId) {
        return Result.success(userFriendChatService.sendChatFile(receiverId, fileId, MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/getUserFriendChatMessages")
    @ApiOperation(value = "获取好友全部聊天信息", tags = "1.0.0")
    public Result<List<UserFriendChatMesagesResp>> getUserFriendChatMessages(@RequestParam(value = "friendId", required = false) @ApiParam(name = "好友用户ID", required = true) @NotNull(message = "好友ID不能为空") Long friendId) {
        return Result.success(userFriendChatService.getUserFriendChatMessages(friendId, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/withdrawChatMessages")
    @ApiOperation(value = "撤回聊天信息", tags = "1.0.0")
    public Result<Boolean> withdrawChatMessages(@RequestBody @Valid @ApiParam(name = "撤回信息请求参数", required = true) WithdrawChatMessageReq req) {
        return Result.success(userFriendChatService.withdrawChatMessages(req, MetaverseContextUtil.getCurrentUserId()));
    }

}