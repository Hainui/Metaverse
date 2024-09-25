package com.metaverse.user.controller;


import com.aliyuncs.exceptions.ClientException;
import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.SendChatAudioReq;
import com.metaverse.user.req.SendChatRecordReq;
import com.metaverse.user.service.UserChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;

/**
 * <p>
 * 聊天记录表 文件上传和下载路径
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@RestController
@RequestMapping("/messageHistory")
@RequiredArgsConstructor
@Validated
public class MetaverseChatRecordController {

    private final UserChatService userChatService;

    @PostMapping("/sendChatMessages")
    @ApiOperation(value = "发送聊天信息(文本或图片)", tags = "1.0.0")
    public Result<Boolean> sendChatMessages(@RequestBody @Valid @ApiParam(name = "发送聊天信息参数", required = true) SendChatRecordReq req) {
        return Result.success(userChatService.sendChatMessages(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/sendChatAudio")
    @ApiOperation(value = "发送聊天音频", tags = "1.0.0")
    public Result<Boolean> sendChatAudio(@RequestBody @Valid @ApiParam(name = "发送音频请求参数", required = true) SendChatAudioReq req) {
        return Result.success(userChatService.sendChatAudio(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/sendChatFile")
    @ApiOperation(value = "发送聊天文件", tags = "1.0.0")
    public Result<Boolean> sendChatFile(@RequestParam("receiverId") @ApiParam(name = "接收信息的用户ID", required = true) @NotNull(message = "接收信息的用户ID不能为空") Long receiverId,
                                        @RequestParam("messageType") @ApiParam(name = "信息的类型", required = true) @NotNull(message = "信息类型不能为空") Byte messageType,
                                        @RequestParam("file") @ApiParam(name = "信息文件", required = true) @NotNull(message = "信息文件不能为空") MultipartFile file) throws IOException, ClientException {
        return Result.success(userChatService.sendChatFile(receiverId, messageType, file, MetaverseContextUtil.getCurrentUserId()));
    }

}