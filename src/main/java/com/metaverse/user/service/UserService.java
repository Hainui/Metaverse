package com.metaverse.user.service;

import com.metaverse.common.Utils.JwtUtils;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.req.MetaverseUserLoginReq;
import com.metaverse.user.req.MetaverseUserRegistrationReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {


    public String login(MetaverseUserLoginReq metaverseUserLoginReq) {
        boolean isLoginSuccess = MetaverseUser.login(metaverseUserLoginReq.getEmail(), metaverseUserLoginReq.getPassword(), metaverseUserLoginReq.getRegionId());
        if (!isLoginSuccess) {
            throw new IllegalArgumentException("登陆失败!");
        }
        // todo 给令牌
        Map<String,Object> claims = new HashMap<>();
        claims.put("username",metaverseUserLoginReq.getEmail());
        claims.put("required",metaverseUserLoginReq.getRegionId());
        String jwt = JwtUtils.generateJwt(claims);//生成一个包含用户信息的Jwt令牌
        return jwt;
    }


    public boolean registration(MetaverseUserRegistrationReq metaverseUserRegistrationReq) {
        return MetaverseUser.registration(metaverseUserRegistrationReq.getName(), metaverseUserRegistrationReq.getEmail(), metaverseUserRegistrationReq.getPassword(), metaverseUserRegistrationReq.getRegionId(), MetaverseUser.Gender.fromValue(metaverseUserRegistrationReq.getGender()));
    }
}
