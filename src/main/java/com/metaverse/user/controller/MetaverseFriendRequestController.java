package com.metaverse.user.controller;

import com.metaverse.common.model.Result;
import com.metaverse.user.req.AddFriendReq;
import com.metaverse.user.service.FriendRequestService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
public class MetaverseFriendRequestController {

    private final FriendRequestService friendRequestService;

    @PostMapping("/addFriend")
    @ApiOperation(value = "加好友请求", tags = "1.0.0")
    public Result<String> addFriend(@ApiParam(name = "添加好友请求参数", required = true) @RequestBody @Valid AddFriendReq req) {
        return Result.success(friendRequestService.addFriend(req));
    }

}
