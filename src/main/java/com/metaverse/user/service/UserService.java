package com.metaverse.user.service;

import cn.hutool.core.collection.CollectionUtil;
import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.config.RedisServer;
import com.metaverse.common.constant.UserConstant;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.db.service.IMetaverseUserService;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.dto.MetaverseUserInfo;
import com.metaverse.user.req.MetaverseUserLoginReq;
import com.metaverse.user.req.MetaverseUserModifyPasswordReq;
import com.metaverse.user.req.MetaverseUserRegistrationReq;
import com.metaverse.user.req.ModifyUserNameReq;
import com.metaverse.user.resp.SearchUserByNameResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final IMetaverseUserService userService;
    private final RedisServer redisServer;

    @Transactional(rollbackFor = Exception.class)
    public String login(MetaverseUserLoginReq metaverseUserLoginReq, String ipAddress) {
        MetaverseUserInfo userInfo = MetaverseUser.login(metaverseUserLoginReq.getEmail(), metaverseUserLoginReq.getPassword(), metaverseUserLoginReq.getRegionId());
        Map<String, Object> claims = new HashMap<>();
        claims.put(UserConstant.METAVERSE_USER, userInfo);
        claims.put(UserConstant.IP_ADDRESS, ipAddress);
        String token = MetaverseContextUtil.generateJwt(claims);
        redisServer.storeToken(userInfo.getId(), token);
        return token;
    }

    public Boolean signOut(Long userId) {
        redisServer.removeToken(userId);
        return Boolean.TRUE;
    }

    public Boolean signOut(List<Long> userIds) {
        if (CollectionUtil.isNotEmpty(userIds)) {
            userIds.forEach(redisServer::removeToken);
        }
        return Boolean.TRUE;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean registration(MetaverseUserRegistrationReq metaverseUserRegistrationReq) {
        return MetaverseUser.registration(metaverseUserRegistrationReq.getName(), metaverseUserRegistrationReq.getEmail(), metaverseUserRegistrationReq.getPassword(), metaverseUserRegistrationReq.getRegionId(), MetaverseUser.Gender.fromValue(metaverseUserRegistrationReq.getGender()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyUserName(ModifyUserNameReq req, Long currentUserId, Long currentRegionId) {
        MetaverseUser metaverseUser = MetaverseUser.writeLoadAndAssertNotExist(req.getUserId(), currentRegionId);
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
                .setAvatarFileId(userDO.getAvatarFileId())
                .setBirthTime(userDO.getBirthTime());
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean metaverseUserModifyPassword(MetaverseUserModifyPasswordReq req, Long currentUserId, Long currentRegionId) {
        MetaverseUser metaverseUser = MetaverseUser.writeLoadAndAssertNotExist(req.getUserId(), currentRegionId);
        return metaverseUser.modifyPassword(req, currentUserId);
    }

    public Long findRegionIdByUserId(Long userId) {
        MetaverseUserDO userInfo = userService.getById(userId);
        if (Objects.isNull(userInfo)) {
            return null;
        }
        return userInfo.getRegionId();
    }

    public Boolean setAvatarImage(Long currentUserId, Long currentRegion, Long fileId) {
        //判断用户是否存在
        MetaverseUser metaverseUser = MetaverseUser.writeLoadAndAssertNotExist(currentUserId, currentRegion);
        return metaverseUser.setAvatarImage(currentUserId, fileId);

    }
}
