package com.metaverse.user.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.file.dto.FileDto;
import com.metaverse.user.req.GroupChatFileReq;
import com.metaverse.user.req.GroupSendChatRecordReq;
import com.metaverse.user.req.withdrawGroupChatMessagesReq;
import com.metaverse.user.resp.GroupChatMessagesResp;
import com.metaverse.user.service.UserGroupChatService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

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


    @PostMapping("/sendGroupChatMessages")
    @ApiOperation(value = "发送群组聊天信息(文本或图片)", tags = "1.0.0")
    public Result<Boolean> sendChatMessages(@RequestBody @Valid @ApiParam(name = "发送聊天信息参数", required = true) GroupSendChatRecordReq req) {
        return Result.success(userGroupChatService.sendChatMessages(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/sendGroupChatFile")
    @ApiOperation(value = "发送群聊文件", tags = "1.0.0")
    public Result<Boolean> sendGroupChatFile(@RequestBody @Valid @ApiParam(name = "发送聊天文件参数", required = true) GroupChatFileReq req) {
        return Result.success(userGroupChatService.sendGroupChatFile(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/sendGroupChatAudio")
    @ApiOperation(value = "发送群组聊天音频", tags = "1.0.0")
    public Result<Boolean> sendGroupChatAudio(@RequestBody @Valid @ApiParam(name = "发送聊天文件参数", required = true) GroupChatFileReq req) {
        return Result.success(userGroupChatService.sendGroupChatAudio(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/getGroupChatFile")
    @ApiOperation(value = "获取群聊文件", tags = "1.0.0")
    public Result<List<FileDto>> getGroupChatFile(@RequestParam("groupId") @ApiParam(value = "群组ID", required = true) Long groupId) {
        return Result.success(userGroupChatService.getGroupChatFile(groupId, MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/getGroupChatMessages")
    @ApiOperation(value = "获取群组聊天信息", tags = "1.0.0")
    public Result<List<GroupChatMessagesResp>> getGroupChatMessages(@RequestParam("groupId") @ApiParam(value = "群组ID", required = true) Long groupId) {
        return Result.success(userGroupChatService.getGroupChatMessages(groupId, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/withdrawGroupChatMessages")
    @ApiOperation(value = "撤回群组的聊天信息", tags = "1.0.0")
    public Result<Boolean> withdrawGroupChatMessages(@RequestBody @Valid @ApiParam(name = "撤回信息请求参数", required = true) withdrawGroupChatMessagesReq req) {
        return Result.success(userGroupChatService.withdrawGroupChatMessages(req, MetaverseContextUtil.getCurrentUserId()));
    }

}
