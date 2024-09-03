package com.metaverse.user.repository;

import com.metaverse.user.db.entity.MetaverseUserDO;

public interface MetaverseUserRepository {

    boolean existByName(String name, Long regionId);

    boolean save(MetaverseUserDO userDO);
}
