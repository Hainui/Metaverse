package com.metaverse.region.repository;

import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.domain.MetaverseRegion;

import java.util.List;

public interface MetaverseRegionRepository {


    boolean save(MetaverseRegionDO metaverseRegionDO);

    boolean existByName(String name);

    Boolean updateRegionName(Long id, String name, Long currentUserId, Long version);

    MetaverseRegion findByIdWithWriteLock(Long id);

    Boolean existByRegionId(Long regionId);

    Boolean modifyRegionLocationList(List<String> newServerLocation, Long regionId, Long currentUserId, Long newVersion);
}
