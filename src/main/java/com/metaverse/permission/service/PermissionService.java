package com.metaverse.permission.service;

import com.metaverse.permission.db.entity.MetaversePermissionDO;
import com.metaverse.permission.db.service.IMetaversePermissionService;
import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipDeleteService;
import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipService;
import com.metaverse.permission.req.ModifyPermissionNameReq;
import com.metaverse.permission.req.ModifyPermissionReq;
import com.metaverse.permission.req.PermissionCreateReq;
import com.metaverse.permission.resp.MetaversePermissionResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PermissionService {

    private final IMetaversePermissionService permissionService;
    private final IMetaverseUserPermissionRelationshipService permissionRelationshipService;
    private final IMetaverseUserPermissionRelationshipDeleteService permissionRelationshipDeleteService;

    public List<MetaversePermissionResp> getAllMetaversePermission() {
        List<MetaversePermissionDO> list = permissionService.lambdaQuery().list();
        return list.stream().map(this::convertPermissionResp).collect(Collectors.toList());
    }

    private MetaversePermissionResp convertPermissionResp(MetaversePermissionDO metaversePermissionDO) {
        if (Objects.isNull(metaversePermissionDO)) {
            return null;
        }
        return new MetaversePermissionResp()
                .setId(metaversePermissionDO.getId())
                .setPermissionGroupName(metaversePermissionDO.getPermissionGroupName());
    }

    public Long create(PermissionCreateReq req, Long currentUserId) {
        return null;
    }

    public Boolean modifyPermissionName(ModifyPermissionNameReq req, Long currentUserId) {
        return null;
    }

    public Boolean modifyPermissions(ModifyPermissionReq req, Long currentUserId) {
        return null;
    }
}
