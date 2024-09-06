package com.metaverse.user.domain.region.repository;

import com.metaverse.user.domain.region.db.entity.MetaverseRegionDO;
import com.metaverse.user.domain.region.domain.MetaverseRegion;

public interface MetaverseRegionRepository {


    boolean save(MetaverseRegionDO metaverseRegionDO);

    boolean existByName(String name);

    Boolean updateRegionName(Long id, String name, Long currentUserId, Long version);


    MetaverseRegion findByIdWithLock(Long id);
}
