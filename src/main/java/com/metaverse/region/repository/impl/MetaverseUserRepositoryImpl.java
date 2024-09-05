package com.metaverse.region.repository.impl;

import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.db.service.IMetaverseRegionService;
import com.metaverse.region.repository.MetaverseRegionRepository;
import com.metaverse.user.db.service.IMetaverseUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
public class MetaverseUserRepositoryImpl implements MetaverseRegionRepository {

    private final IMetaverseUserService userService;
    private final IMetaverseRegionService regionService;

    @Override
    public boolean save(MetaverseRegionDO metaverseRegionDO) {
        return regionService.save(metaverseRegionDO);
    }
}






















