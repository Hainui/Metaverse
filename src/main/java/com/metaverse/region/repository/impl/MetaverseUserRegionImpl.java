package com.metaverse.region.repository.impl;

import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.db.service.IMetaverseRegionService;
import com.metaverse.region.repository.MetaverseRegionRepository;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.db.service.IMetaverseUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
public class MetaverseUserRegionImpl implements MetaverseRegionRepository {//连接数据库操作,调用Iservice接口的方法

    private final IMetaverseUserService userService;
    private final IMetaverseRegionService regionService;

    @Override
    public boolean save(MetaverseRegionDO metaverseRegionDO) {
        return regionService.save(metaverseRegionDO);
    }

    @Override
    public boolean existByName(String name, Long id) {
        return regionService.lambdaQuery()
                        .eq(MetaverseRegionDO::getId, id)
                        .eq(MetaverseRegionDO::getName, name)
                        .last("LIMIT 1")
                        .count() > 0;
    }

    @Override
    public Boolean updateRegionName(Long id, String name, Long currentUserId) {

        boolean success = regionService.lambdaUpdate()
                .eq(MetaverseRegionDO::getId, id)
                .set(MetaverseRegionDO::getName, name)
                .update();
        return success;
    }

    @Override
    public boolean findByIdWithLock(Long id) {
        return regionService.lambdaQuery().eq(MetaverseRegionDO::getId, id).exists();
    }
}






















