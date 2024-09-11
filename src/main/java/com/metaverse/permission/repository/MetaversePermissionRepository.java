package com.metaverse.permission.repository;

import com.metaverse.permission.db.entity.MetaversePermissionDO;
import com.metaverse.permission.db.entity.MetaverseUserPermissionRelationshipDeleteDO;
import com.metaverse.permission.domain.MetaversePermission;

import java.util.List;

public interface MetaversePermissionRepository {
    boolean save(MetaversePermissionDO metaversePermissionDO);

    boolean existByName(String name);


    Boolean updatePermissionName(Long pkVal, String name, Long currentUserId, Long newVersion);

    MetaversePermission findByIdWithWriteLock(Long id);

    MetaversePermission findByIdWithReadLock(Long id);

    Boolean modifyPermissions(List<String> newPermissions, Long pkVal, Long currentUserId, Long newVersion, List<String> oldPermissions);

    boolean deleteAllUserIdPermission(Long userId);

    boolean backupDeleteAllUserIdPermission(MetaverseUserPermissionRelationshipDeleteDO metaverseUserPermissionRelationshipDeleteDO);

    boolean saveUserPermission(List<Long> permissionIds, List<Long> userIds, Long currentUserId);


    boolean deleteUserIdPermission(List<Long> userId, List<Long> permissionIds);

    boolean deleteOneUserPermission(Long userId, List<Long> permissionIds);


}
