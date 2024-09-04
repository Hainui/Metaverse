package com.metaverse.user.service;

import com.metaverse.common.Utils.JwtUtils;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.req.MetaverseUserLoginReq;
import com.metaverse.user.req.MetaverseUserRegistrationReq;
import com.metaverse.user.req.ModifyUserNameReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {


    @Transactional
    public String login(MetaverseUserLoginReq metaverseUserLoginReq) {
        Long userId = MetaverseUser.login(metaverseUserLoginReq.getEmail(), metaverseUserLoginReq.getPassword(), metaverseUserLoginReq.getRegionId());
        // todo 给令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", metaverseUserLoginReq.getEmail());
        claims.put("regionId", metaverseUserLoginReq.getRegionId());
        claims.put("userId", userId);
        return JwtUtils.generateJwt(claims);
    }

    @Transactional
    public boolean registration(MetaverseUserRegistrationReq metaverseUserRegistrationReq) {
        return MetaverseUser.registration(metaverseUserRegistrationReq.getName(), metaverseUserRegistrationReq.getEmail(), metaverseUserRegistrationReq.getPassword(), metaverseUserRegistrationReq.getRegionId(), MetaverseUser.Gender.fromValue(metaverseUserRegistrationReq.getGender()));
    }

    @Transactional
    public Boolean modifyUserName(ModifyUserNameReq name) {
        return null;
    }
}
