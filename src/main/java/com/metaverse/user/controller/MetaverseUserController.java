package com.metaverse.user.controller;

import com.metaverse.common.Utils.HttpUtils;
import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.Utils.VerificationCodeUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.MetaverseUserLoginReq;
import com.metaverse.user.req.MetaverseUserModifyPasswordReq;
import com.metaverse.user.req.MetaverseUserRegistrationReq;
import com.metaverse.user.req.ModifyUserNameReq;
import com.metaverse.user.resp.SearchUserByNameResp;
import com.metaverse.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 元宇宙用户表 前端控制器
 * </p>
 *
 * @author Hainui
 * @since 2024-09-03
 */
@RestController
@RequestMapping("/metaverseUser")
@RequiredArgsConstructor
public class MetaverseUserController {

    private final UserService userService;

    @PostMapping("/login")
    @ApiOperation(value = "用户登录", tags = "1.0.0")
    public Result<String> login(@ApiParam(name = "用户登录请求参数", required = true) @RequestBody @Valid MetaverseUserLoginReq req, HttpServletRequest request) {
        return Result.success(userService.login(req, HttpUtils.getIpAddress(request)));
    }

    @GetMapping("/signOut")
    @ApiOperation(value = "用户退出登录", tags = "1.0.0")
//    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.USER}, action = PermissionConstant.Action.DELETE)
    public Result<Boolean> signOut() {
        return Result.success(userService.signOut(MetaverseContextUtil.getCurrentUserId()));
    }

    @GetMapping("/searchUser")
    @ApiOperation(value = "根据用户名精确查找用户", tags = "1.0.0")
//    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.USER}, action = PermissionConstant.Action.READ)
    public Result<SearchUserByNameResp> searchUser(@ApiParam(name = "用户搜索参数", required = true) @RequestParam(value = "userName") @NotBlank(message = "用户名不能为空") String userName) {
        return Result.success(userService.searchUserByName(userName, MetaverseContextUtil.getCurrentUserRegion().getId()));
    }

    @PostMapping("/registration")
    @ApiOperation(value = "用户注册", tags = "1.0.0")
    public Result<Boolean> registration(@ApiParam(name = "用户注册请求参数", required = true) @RequestBody @Valid MetaverseUserRegistrationReq req) {
        boolean isValid = VerificationCodeUtil.verifyCode(req.getEmail(), req.getVerifyCode());
        if (!isValid) {
            throw new IllegalArgumentException("验证失败");
        }
        return Result.success(userService.registration(req));
    }

    @GetMapping("/registrationSendVerificationCode")
    @ApiOperation(value = "发送验证码", tags = "1.0.0")
    public Result<Void> sendVerificationCode(@ApiParam(name = "请求发送验证码的邮箱", required = true) @RequestParam(value = "email", required = false) @NotBlank(message = "邮箱不能为空") String email) {
        VerificationCodeUtil.sendVerificationCode(email);
        return Result.success();
    }

    @PostMapping("/modifyUserName")
    @ApiOperation(value = "修改用户名", tags = "1.0.0")
//    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.USER}, action = PermissionConstant.Action.UPDATE)
    public Result<Boolean> modifyUserName(@ApiParam(name = "用户名修改请求参数", required = true) @RequestBody @Valid ModifyUserNameReq req) {
        return Result.success(userService.modifyUserName(req, MetaverseContextUtil.getCurrentUserId(), MetaverseContextUtil.getCurrentUserRegion().getId()));
    }


    @PostMapping("/metaverseUserModifyPassword")
    @ApiOperation(value = "用户修改密码", tags = "1.0.0")
//    @Permission(resourceTypeElements = {PermissionConstant.ResourceType.USER}, action = PermissionConstant.Action.UPDATE)
    public Result<Boolean> metaverseUserModifyPassword(@ApiParam(name = "用户修改密码请求参数", required = true) @RequestBody @Valid MetaverseUserModifyPasswordReq req) {
        return Result.success(userService.metaverseUserModifyPassword(req, MetaverseContextUtil.getCurrentUserId(), MetaverseContextUtil.getCurrentUserRegion().getId()));
    }


}
