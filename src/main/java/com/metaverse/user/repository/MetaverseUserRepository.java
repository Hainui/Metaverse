package com.metaverse.user.repository;

import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.domain.MetaverseUser;

import java.util.List;

public interface MetaverseUserRepository {

    boolean existByName(String name, Long regionId);

    boolean save(MetaverseUserDO userDO);

    MetaverseUser login(String email, String password, Long regionId);

    MetaverseUser findByIdWithWriteLock(Long userId);

    MetaverseUser findByIdWithReadLock(Long userId);

    List<MetaverseUser> findByIdsWithReadLock(List<Long> userIds);

    boolean modifyUserName(Long userId, String name, Long updateBy, Long newVersion);


    boolean modifyPassword(String newPassword, Long userId, Long currentUserId, Long newVersion);

    boolean uploadAvatarImage(Long currentUserId, Long avatarFileId, Long newVersion);
}
