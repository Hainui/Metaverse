package com.metaverse.permission.service;

import com.metaverse.common.Utils.PermissionStrValidator;
import com.metaverse.permission.db.entity.MetaversePermissionDO;
import com.metaverse.permission.db.service.IMetaverseActionEnumService;
import com.metaverse.permission.db.service.IMetaverseLocatorEnumService;
import com.metaverse.permission.db.service.IMetaversePermissionService;
import com.metaverse.permission.db.service.IMetaverseResourceTypeEnumService;
import com.metaverse.permission.domain.MetaversePermission;
import com.metaverse.permission.req.ModifyPermissionNameReq;
import com.metaverse.permission.req.ModifyPermissionReq;
import com.metaverse.permission.req.PermissionCreateReq;
import com.metaverse.permission.resp.MetaversePermissionResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaversePermissionService {

    private final IMetaversePermissionService permissionService;
    private final IMetaverseResourceTypeEnumService resourceTypeEnumService;
    private final IMetaverseActionEnumService actionEnumService;
    private final IMetaverseLocatorEnumService locatorEnumService;

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

    @Transactional(rollbackFor = Exception.class)
    public Long create(PermissionCreateReq req, Long currentUserId) {
        List<String> permissions = req.getPermissions();
        PermissionStrValidator.validatePermissionStrs(permissions);
        Long permissionId = MetaversePermission.create(req.getName(), permissions, currentUserId);
        for (String permission : permissions) {
            String[] permissionStr = permission.split("\\.");
            String resourceType = permissionStr[0];
            String action = permissionStr[1];
            String locator = permissionStr[2];
            // todo 这三个枚举分别要加入三个表中 resourceTypeEnum actionEnum locatorEnum

        }
        return permissionId;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyPermissionName(ModifyPermissionNameReq req, Long currentUserId) {
        MetaversePermission permission = MetaversePermission.writeLoadAndAssertNotExist(req.getId());
        return permission.modifyPermissionName(req.getName(), currentUserId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyPermissions(ModifyPermissionReq req, Long currentUserId) {
        List<String> permissions = req.getPermissions();
        PermissionStrValidator.validatePermissionStrs(permissions);
        MetaversePermission permission = MetaversePermission.writeLoadAndAssertNotExist(req.getId());
        return permission.modifyPermissions(req.getPermissions(), currentUserId);
    }
}
