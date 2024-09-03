package com.metaverse.user.service;

import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.req.MetaverseUserLoginReq;
import com.metaverse.user.req.MetaverseUserRegistrationReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {


    public String login(MetaverseUserLoginReq metaverseUserLoginReq) {
        boolean isLoginSuccess = MetaverseUser.login(metaverseUserLoginReq.getEmail(), metaverseUserLoginReq.getPassword(), metaverseUserLoginReq.getRegionId());
        if(!isLoginSuccess){
            throw new IllegalArgumentException("登陆失败!");
        }
        // 给令牌

//        if (metaverseUserDO1 != null) {
//            Map<String, Object> claims = new HashMap<>();
//            claims.put("id", metaverseUserDO.getId());
//            claims.put("username", metaverseUserDO.getUsername());
//            String jwt = JwtUtils.generateJwt(claims);//生成一个包含用户信息的Jwt令牌
//            return Result.success(jwt);
//        }
//        return Result.error("用户名或密码错误");
        return "";
    }


    public boolean registration(MetaverseUserRegistrationReq metaverseUserRegistrationReq) {
        return MetaverseUser.registration(metaverseUserRegistrationReq.getName(), metaverseUserRegistrationReq.getEmail(), metaverseUserRegistrationReq.getPassword(), metaverseUserRegistrationReq.getRegionId(), MetaverseUser.Gender.fromValue(metaverseUserRegistrationReq.getGender()));
    }
}
