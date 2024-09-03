package com.metaverse.user.controller;

import com.metaverse.common.model.Result;
import com.metaverse.user.req.MetaverseUserLoginReq;
import com.metaverse.user.req.MetaverseUserRegistrationReq;
import com.metaverse.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
    public Result<?> login(@RequestBody @Valid MetaverseUserLoginReq metaverseUserLoginReq) {
        return Result.success(userService.login(metaverseUserLoginReq));
    }

    @PostMapping("/registration")
    public Result<?> registration(@RequestBody @Valid MetaverseUserRegistrationReq metaverseUserLoginReq) {
        return Result.success(userService.registration(metaverseUserLoginReq));
    }

}
