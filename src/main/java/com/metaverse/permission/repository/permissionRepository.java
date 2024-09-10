package com.metaverse.permission.repository;

import com.metaverse.permission.db.entity.MetaversePermissionDO;
import com.metaverse.permission.domain.MetaversePermission;

public interface permissionRepository {
    boolean save(MetaversePermissionDO metaversePermissionDO);

    boolean existByName(String name);


    Boolean updatePermissionName(Long pkVal, String name, Long currentUserId, Long newVersion);

    MetaversePermission findById(Long id);
}
