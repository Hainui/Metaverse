package com.metaverse.user.repository;

import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.domain.MetaverseUser;

public interface MetaverseUserRepository {

    boolean existByName(String name, Long regionId);

    boolean save(MetaverseUserDO userDO);

    MetaverseUser login(String email, String password, Long regionId);

    boolean existByRegionId(Long regionId);

    MetaverseUser findByIdWithLock(Long userId);

    boolean modifyUserName(Long userId, String name, Long updateBy, Long version);
}
