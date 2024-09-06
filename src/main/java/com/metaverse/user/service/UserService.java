package com.metaverse.user.service;

import com.metaverse.common.Utils.JwtUtils;
import com.metaverse.common.constant.UserConstant;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.db.service.IMetaverseUserService;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.req.MetaverseUserLoginReq;
import com.metaverse.user.req.MetaverseUserRegistrationReq;
import com.metaverse.user.req.ModifyUserNameReq;
import com.metaverse.user.resp.SearchUserByNameResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    IMetaverseUserService userService;

    @Transactional(rollbackFor = Exception.class)
    public String login(MetaverseUserLoginReq metaverseUserLoginReq) {
        MetaverseUser user = MetaverseUser.login(metaverseUserLoginReq.getEmail(), metaverseUserLoginReq.getPassword(), metaverseUserLoginReq.getRegionId());
        Map<String, Object> claims = new HashMap<>();
        claims.put(UserConstant.EMAIL, metaverseUserLoginReq.getEmail());
        claims.put(UserConstant.REGION_ID, metaverseUserLoginReq.getRegionId());
        claims.put(UserConstant.USER_ID, user.getId());
        claims.put(UserConstant.USER_NAME, user.getName());
        claims.put(UserConstant.USER_NAME, user.getName());
        return JwtUtils.generateJwt(claims);
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean registration(MetaverseUserRegistrationReq metaverseUserRegistrationReq) {
        return MetaverseUser.registration(metaverseUserRegistrationReq.getName(), metaverseUserRegistrationReq.getEmail(), metaverseUserRegistrationReq.getPassword(), metaverseUserRegistrationReq.getRegionId(), MetaverseUser.Gender.fromValue(metaverseUserRegistrationReq.getGender()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyUserName(ModifyUserNameReq req, Long currentUserId) {
        MetaverseUser metaverseUser = MetaverseUser.loadAndAssertNotExist(req.getUserId());
        return metaverseUser.modifyUserName(req, currentUserId);
    }

    public SearchUserByNameResp searchUserByName(String userName, Long regionId) {
        MetaverseUserDO userDO = userService.lambdaQuery()
                .eq(MetaverseUserDO::getUsername, userName)
                .eq(MetaverseUserDO::getRegionId, regionId)
                .one();
        return userDOConvertResp(userDO);

    }

    private SearchUserByNameResp userDOConvertResp(MetaverseUserDO userDO) {
        if (Objects.isNull(userDO)) {
            return null;
        }
        return new SearchUserByNameResp()
                .setName(userDO.getUsername())
                .setUserId(userDO.getId())
                .setGender(MetaverseUser.Gender.convertGender(userDO.getGender()))
                .setBirthTime(userDO.getBirthTime());
    }
}
