package com.metaverse.permission.repository.impI;

import com.alibaba.fastjson.JSONArray;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.permission.db.entity.MetaversePermissionDO;
import com.metaverse.permission.db.service.IMetaversePermissionService;
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

    @Override
    public boolean save(MetaversePermissionDO metaversePermissionDO) {
        return permissionService.save(metaversePermissionDO);
    }

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

    @Override
    public MetaversePermission findById(Long id) {
        MetaversePermissionDO entity = permissionService.lambdaQuery()
                .eq(MetaversePermissionDO::getId,id)
                .last(RepositoryConstant.FOR_UPDATE)
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

        String serverLocationJson = metaversePermissionDO.getPermissions();
        List<String> serverLocations = JSONArray.parseArray(serverLocationJson, String.class);
        permission.setPermissions(serverLocations);
        permission.setCreateBy(metaversePermissionDO.getCreateBy());
        permission.setCreateAt(metaversePermissionDO.getCreateAt());
        permission.setUpdatedBy(metaversePermissionDO.getUpdateBy());
        permission.setUpdatedAt(metaversePermissionDO.getUpdateAt());
        permission.setVersion(metaversePermissionDO.getVersion());
        return permission;
    }

}
