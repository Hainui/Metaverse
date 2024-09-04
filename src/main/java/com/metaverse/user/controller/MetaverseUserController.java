package com.metaverse.user.controller;

import com.metaverse.common.Utils.VerificationCodeUtil;
import com.metaverse.common.model.Result;
import com.metaverse.user.req.MetaverseUserLoginReq;
import com.metaverse.user.req.MetaverseUserRegistrationReq;
import com.metaverse.user.service.UserService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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


    @PostMapping("/modifyUserName")
    @ApiOperation(value = "用户登录", tags = "1.0.0")
    public Result<String> login(@RequestBody @Valid MetaverseUserLoginReq metaverseUserLoginReq) {
        return Result.success(userService.login(metaverseUserLoginReq));
    }

    @PostMapping("/registration")
    @ApiOperation(value = "用户注册", tags = "1.0.0")
    public Result<Boolean> registration(@RequestBody @Valid MetaverseUserRegistrationReq metaverseUserLoginReq) {
        boolean isValid = VerificationCodeUtil.verifyCode(metaverseUserLoginReq.getEmail(), metaverseUserLoginReq.getVerifyCode());
        if (!isValid) {
            throw new IllegalArgumentException("验证失败");
        }
        return Result.success(userService.registration(metaverseUserLoginReq));
    }


    @GetMapping("/registration/sendVerificationCode")
    @ApiOperation(value = "发送验证码", tags = "1.0.0")
    public Result<Void> sendVerificationCode(@RequestParam(value = "email", required = false) @NotBlank(message = "邮箱不能为空") String email) {
        VerificationCodeUtil.sendVerificationCode(email);
        return Result.success();
    }

    @PutMapping("/login")
    @ApiOperation(value = "修改用户名", tags = "1.0.0")
    public Result<Boolean> modifyUserName(@RequestBody @Valid String name) {
        return Result.success(userService.modifyUserName(name));
    }

}
