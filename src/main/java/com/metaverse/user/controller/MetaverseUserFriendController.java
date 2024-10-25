package com.metaverse.user.controller;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.dto.MetaverseUserAbstractInfo;
import com.metaverse.user.req.AddFriendReq;
import com.metaverse.user.req.AnswerUserQuestionReq;
import com.metaverse.user.resp.MetaverseFriendRequestResp;
import com.metaverse.user.resp.RecommendFriendResp;
import com.metaverse.user.resp.UserFriendQuestionResp;
import com.metaverse.user.service.UserFriendService;
import io.swagger.annotations.Api;
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
 * 好友请求表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@RestController
@RequestMapping("/metaverseFriendRequest")
@RequiredArgsConstructor
@Validated
@Api("好友请求")
public class MetaverseUserFriendController {

    private final UserFriendService userFriendService;

    @PostMapping("/addFriendRequest")
    @ApiOperation(value = "加好友请求", tags = "1.0.0")
    public Result<UserFriendQuestionResp> addFriendRequest(@RequestBody @Valid @ApiParam(name = "添加好友请求参数", required = true) AddFriendReq req) {
        return Result.success(userFriendService.addFriendRequest(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/delFriend")
    @ApiOperation(value = "删除好友", tags = "1.0.0")
    public Result<Boolean> delFriend(@RequestParam("targetId") @ApiParam(name = "被删除好友ID", required = true) @NotNull(message = "被删除好友ID不能为空") Long targetId) {
        return Result.success(userFriendService.delFriend(targetId, MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/recommendFriendsForCurrentUser")
    @ApiOperation(value = "为当前登录用户推荐好友", tags = "1.0.0")
    public Result<List<RecommendFriendResp>> recommendFriendsForCurrentUser() {
        return Result.success(userFriendService.recommendFriendsForCurrentUser(MetaverseContextUtil.getCurrentUserId(), MetaverseContextUtil.getCurrentUserRegion().getId()));
    }

    @GetMapping("/blockFriend")
    @ApiOperation(value = "拉黑好友", tags = "1.0.0")
    public Result<Boolean> blockFriend(@RequestParam("targetId") @ApiParam(name = "被拉黑好友ID", required = true) @NotNull(message = "被拉黑好友ID不能为空") Long targetId) {
        return Result.success(userFriendService.blockFriend(targetId, MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/unblockFriends")
    @ApiOperation(value = "解除拉黑的好友", tags = "1.0.0")
    public Result<Boolean> unblockFriends(@RequestParam("targetId") @ApiParam(name = "被解除拉黑好友ID", required = true) @NotNull(message = "被解除拉黑好友ID不能为空") Long targetId) {
        return Result.success(userFriendService.unblockFriends(targetId, MetaverseContextUtil.getCurrentUserId()));
    }

    @PostMapping("/answerUserQuestion")
    @ApiOperation(value = "回答问题", tags = "1.0.0")
    public Result<Boolean> answerUserQuestion(@RequestBody @Valid @ApiParam(name = "回答用户问题请求参数", required = true) AnswerUserQuestionReq req) {
        return Result.success(userFriendService.answerUserQuestion(req, MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/getUnagreedFriendRequestsOnYourself")
    @ApiOperation(value = "获取所有对我的好友请求(未同意)", tags = "1.0.0")
    public Result<List<MetaverseFriendRequestResp>> getUnagreedFriendRequestsOnYourself() {
        return Result.success(userFriendService.getFriendRequestsOnYourself(MetaverseContextUtil.getCurrentUserId(), 0));
    }

    @GetMapping("/getFriendRequestsOnYourself")
    @ApiOperation(value = "获取所有对我的好友请求(所有)", tags = "1.0.0")
    public Result<List<MetaverseFriendRequestResp>> getFriendRequestsOnYourself() {
        return Result.success(userFriendService.getFriendRequestsOnYourself(MetaverseContextUtil.getCurrentUserId(), null));
    }

    @GetMapping("/agreeFriendRequest")
    @ApiOperation(value = "同意该好友请求", tags = "1.0.0")
    public Result<Boolean> agreeFriendRequest(@RequestParam(value = "senderId", required = false) @ApiParam(name = "请求方用户ID", required = true) @NotNull(message = "请求方用户ID不能为空") Long senderId) {
        return Result.success(userFriendService.agreeFriendRequest(MetaverseContextUtil.getCurrentUserId(), senderId));
    }

    @GetMapping("/rejectFriendRequest")
    @ApiOperation(value = "拒绝该好友请求", tags = "1.0.0")
    public Result<Boolean> rejectFriendRequest(@RequestParam(value = "senderId", required = false) @ApiParam(name = "请求方用户ID", required = true) @NotNull(message = "请求方用户ID不能为空") Long senderId) {
        return Result.success(userFriendService.rejectFriendRequest(MetaverseContextUtil.getCurrentUserId(), senderId));
    }

    @GetMapping("targetUserIsFriend")
    @ApiOperation(value = "判断当前登录用户和目标用户是否是好友", tags = "1.0.0")
    public Result<Boolean> targetUserIsFriend(@RequestParam(value = "targetId", required = false) @ApiParam(name = "目标用户ID", required = true) @NotNull(message = "目标用户ID不能为空") Long targetId) {
        return Result.success(userFriendService.targetUserIsFriend(MetaverseContextUtil.getCurrentUserId(), targetId));
    }

    @GetMapping("/getAllFriends")
    @ApiOperation(value = "获取当前用户的所有好友列表", tags = "1.0.0")
    public Result<List<MetaverseUserAbstractInfo>> getAllFriends() {
        return Result.success(userFriendService.getAllFriends(MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/getAllBlackUsers")
    @ApiOperation(value = "获取当前用户的所有黑名单列表", tags = "1.0.0")
    public Result<List<MetaverseUserAbstractInfo>> getAllBlackUsers() {
        return Result.success(userFriendService.getAllBlackUsers(MetaverseContextUtil.getCurrentUserId()));
    }
}
