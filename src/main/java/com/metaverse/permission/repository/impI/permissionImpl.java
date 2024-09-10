package com.metaverse.permission.repository.impI;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.permission.db.entity.MetaversePermissionDO;
import com.metaverse.permission.db.service.IMetaverseActionEnumService;
import com.metaverse.permission.db.service.IMetaverseLocatorEnumService;
import com.metaverse.permission.db.service.IMetaversePermissionService;
import com.metaverse.permission.db.service.IMetaverseResourceTypeEnumService;
import com.metaverse.permission.domain.MetaversePermission;
import com.metaverse.permission.repository.permissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Repository
public class permissionImpl implements permissionRepository {
    private final IMetaversePermissionService permissionService;
    private final IMetaverseResourceTypeEnumService resourceTypeEnumService;
    private final IMetaverseActionEnumService actionEnumService;
    private final IMetaverseLocatorEnumService locatorEnumService;

    @Override
    public boolean save(MetaversePermissionDO metaversePermissionDO) {
        boolean saved = permissionService.save(metaversePermissionDO);
        String permissionJson = metaversePermissionDO.getPermissions();
        List<String> permissionList = JSONArray.parseArray(permissionJson, String.class);
        for (String permission : permissionList) {
            String[] permissionStr = permission.split("\\.");
            String resourceType = permissionStr[0];
            String action = permissionStr[1];
            String locator = permissionStr[2];
            // todo 这三个枚举分别要加入三个表中 resourceTypeEnum actionEnum locatorEnum

        }
        return saved;


    }

    // 快照读
    @Override
    public boolean existByName(String name) {
        return permissionService.lambdaQuery()
                .eq(MetaversePermissionDO::getPermissionGroupName, name)
                .last(RepositoryConstant.LIMIT_ONE)
                .count() > 0;
    }

    @Override
    public Boolean updatePermissionName(Long id, String name, Long currentUserId, Long version) {
        return permissionService.lambdaUpdate()
                .eq(MetaversePermissionDO::getId, id)
                .set(MetaversePermissionDO::getPermissionGroupName, name)
                .set(MetaversePermissionDO::getUpdateBy, currentUserId)
                .set(MetaversePermissionDO::getVersion, version)
                .update();
    }

    // 排它锁
    @Override
    public MetaversePermission findByIdWithWriteLock(Long id) {
        MetaversePermissionDO entity = permissionService.lambdaQuery()
                .eq(MetaversePermissionDO::getId, id)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        return covertFromDo(entity);
    }

    // 共享锁
    @Override
    public MetaversePermission findByIdWithReadLock(Long id) {
        MetaversePermissionDO entity = permissionService.lambdaQuery()
                .eq(MetaversePermissionDO::getId, id)
                .last(RepositoryConstant.LOCK_IN_SHARE_MODE)
                .one();
        return covertFromDo(entity);
    }


    public static MetaversePermission covertFromDo(MetaversePermissionDO metaversePermissionDO) {
        if (Objects.isNull(metaversePermissionDO)) {
            return null;
        }
        MetaversePermission permission = new MetaversePermission();
        permission.setId(metaversePermissionDO.getId());
        permission.setPermissionGroupName(metaversePermissionDO.getPermissionGroupName());

        String permissions = metaversePermissionDO.getPermissions();
        permission.setPermissions(JSONArray.parseArray(permissions, String.class));
        permission.setCreateBy(metaversePermissionDO.getCreateBy());
        permission.setCreateAt(metaversePermissionDO.getCreateAt());
        permission.setUpdatedBy(metaversePermissionDO.getUpdateBy());
        permission.setUpdatedAt(metaversePermissionDO.getUpdateAt());
        permission.setVersion(metaversePermissionDO.getVersion());
        return permission;
    }

    @Override
    public Boolean modifyPermissions(List<String> newPermissions, Long id, Long currentUserId, Long newVersion, List<String> oldPermissions) {
        boolean updated = permissionService.lambdaUpdate()
                .eq(MetaversePermissionDO::getId, id)
                .set(MetaversePermissionDO::getPermissions, JSON.toJSONString(newPermissions))
                .set(MetaversePermissionDO::getUpdateBy, currentUserId)
                .set(MetaversePermissionDO::getVersion, newVersion)
                .update();
        for (String permission : oldPermissions) {
            // 直接等值匹配删除
        }
        for (String permission : newPermissions) {
            // 直接批量插入
        }
        return updated;
    }

}
