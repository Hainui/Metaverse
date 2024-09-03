package com.metaverse.user.repository;

import com.metaverse.user.db.entity.MetaverseUserDO;

public interface MetaverseUserRepository {

    boolean existByName(String name, Long regionId);

    boolean save(MetaverseUserDO userDO);

    boolean login(String email, String password, Long regionId);

    boolean existByRegionId(Long regionId);
}
