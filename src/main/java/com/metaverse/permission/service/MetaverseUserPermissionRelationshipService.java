package com.metaverse.permission.service;

import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipDeleteService;
import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipService;
import com.metaverse.permission.req.AuthoritiesAccreditUsersReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaverseUserPermissionRelationshipService {
    private final IMetaverseUserPermissionRelationshipService permissionRelationshipService;
    private final IMetaverseUserPermissionRelationshipDeleteService permissionRelationshipDeleteService;

    public Boolean authoritiesImpowerUsers(AuthoritiesAccreditUsersReq req) {

        return null;
    }

    public Boolean authoritiesResetUsers(AuthoritiesAccreditUsersReq req) {
        return null;
    }
}
