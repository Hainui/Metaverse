package com.metaverse.permission.repository;

import com.metaverse.permission.db.entity.MetaversePermissionDO;
import com.metaverse.permission.db.entity.MetaverseUserPermissionRelationshipDeleteDO;
import com.metaverse.permission.domain.MetaversePermission;

import java.util.List;

public interface MetaversePermissionRepository {
    boolean save(MetaversePermissionDO metaversePermissionDO, List<String> permissions);

    boolean existByName(String name);

    boolean updatePermissionName(Long pkVal, String name, Long currentUserId, Long newVersion);

    MetaversePermission findByIdWithWriteLock(Long id);

    MetaversePermission findByIdWithReadLock(Long id);

    boolean modifyPermissions(List<String> newPermissions, Long id, Long currentUserId, Long newVersion, List<String> oldPermissions);

    boolean deleteAllUserIdPermission(Long userId);

    boolean backupDeleteAllUserIdPermission(MetaverseUserPermissionRelationshipDeleteDO metaverseUserPermissionRelationshipDeleteDO);

    boolean deleteUserPermissionId(Long id, Long permissionId);

    boolean saveUserPermission(List<Long> permissionIds, List<Long> userIds, Long currentUserId);


    boolean deleteUserIdPermission(List<Long> userId, List<Long> permissionIds);

    boolean deleteOneUserPermission(Long userId, List<Long> permissionIds);


}
