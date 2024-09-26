package com.metaverse.user.repository;

import com.metaverse.user.db.entity.MetaverseUserGroupDO;
import com.metaverse.user.domain.MetaverseUserGroup;

import java.util.List;

public interface MetaverseUserGroupRepository {

    MetaverseUserGroup findByIdWithWriteLock(Long userId);

    MetaverseUserGroup findByIdWithReadLock(Long userId);

    boolean save(MetaverseUserGroupDO metaverseUserGroupDO, List<Long> memberIds);

    boolean modifyGroupInfo(Long groupId, String groupName, String description, Long newVersion, Long currentUserId);
}
