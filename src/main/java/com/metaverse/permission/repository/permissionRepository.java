package com.metaverse.permission.repository;

import com.metaverse.permission.db.entity.MetaversePermissionDO;
import com.metaverse.permission.domain.MetaversePermission;

import java.util.List;

public interface permissionRepository {
    boolean save(MetaversePermissionDO metaversePermissionDO);

    boolean existByName(String name);


    Boolean updatePermissionName(Long pkVal, String name, Long currentUserId, Long newVersion);

    MetaversePermission findByIdWithWriteLock(Long id);

    MetaversePermission findByIdWithReadLock(Long id);

    Boolean modifyPermissions(List<String> permissions, Long pkVal, Long currentUserId, Long newVersion);
}
