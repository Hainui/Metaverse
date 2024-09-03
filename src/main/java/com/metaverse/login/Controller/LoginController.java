package com.metaverse.login.Controller;

import com.metaverse.login.Utils.JwtUtils;
import com.metaverse.login.common.Result;
import com.metaverse.login.entity.MetaverseUserDO;
import com.metaverse.login.service.IMetaverseUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RestController
public class LoginController {

    @Autowired
    private IMetaverseUserService iMetaverseUserService;

    @PostMapping("/login")
    public Result Login(@RequestBody MetaverseUserDO metaverseUserDO){
        log.info("登陆的用户信息:{}",metaverseUserDO);
        //TODO 缺少一个根据用户账号密码和选区查询表单的方法
        MetaverseUserDO metaverseUserDO1 = iMetaverseUserService.login(metaverseUserDO);
        if(metaverseUserDO1 != null){
            Map<String, Object> claims = new HashMap<>();
            claims.put("id",metaverseUserDO.getId());
            claims.put("username",metaverseUserDO.getUsername());
            String jwt = JwtUtils.generateJwt(claims);//生成一个包含用户信息的Jwt令牌
            return Result.success(jwt);
        }
        return Result.error("用户名或密码错误");



    }
}
