package com.metaverse.region.repository;

import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.domain.Region;

public interface MetaverseRegionRepository {


    boolean save(MetaverseRegionDO metaverseRegionDO);

    boolean existByName(String name);

    Boolean updateRegionName(Long id, String name, Long currentUserId, Long version);


    Region findByIdWithLock(Long id);
}
