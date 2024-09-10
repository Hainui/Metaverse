package com.metaverse.permission.service;

import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipDeleteService;
import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipService;
import com.metaverse.permission.req.AuthoritiesForUserReq;
import com.metaverse.permission.req.AuthoritiesForUsersReq;
import com.metaverse.permission.req.UserAuthoritiesPageReq;
import com.metaverse.permission.resp.UserAuthoritiesPageResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaverseUserPermissionRelationshipService {
    private final IMetaverseUserPermissionRelationshipService permissionRelationshipService;
    private final IMetaverseUserPermissionRelationshipDeleteService permissionRelationshipDeleteService;//备份 上面删除的信息下面要留作备份

    public Boolean authoritiesImpowerUsers(AuthoritiesForUsersReq req) {
        return null;
    }

    public Boolean authoritiesResetUsers(AuthoritiesForUsersReq req) {
        return null;
    }

    public Boolean authoritiesRevokeForUsers(AuthoritiesForUsersReq req) {
        return null;
    }

    public Boolean authoritiesRevokeForUser(AuthoritiesForUserReq req) {
        return null;
    }

    public Boolean authoritiesImpowerUser(AuthoritiesForUserReq req) {
        return null;
    }

    public UserAuthoritiesPageResp userAuthoritiesPageView(UserAuthoritiesPageReq req) {
        return null;
    }
}
