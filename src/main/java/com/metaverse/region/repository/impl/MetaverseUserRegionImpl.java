package com.metaverse.region.repository.impl;

import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.db.service.IMetaverseRegionService;
import com.metaverse.region.domain.Region;
import com.metaverse.region.repository.MetaverseRegionRepository;
import com.metaverse.user.db.service.IMetaverseUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Objects;


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
    public Region findByIdWithLock(Long id) {
        MetaverseRegionDO entity = regionService.lambdaQuery()
                .eq(MetaverseRegionDO::getId, id)
                .last("FOR UPDATE")//加锁
                .one();
        return convertFromDO(entity);
    }

    public static Region convertFromDO(MetaverseRegionDO metaverseRegionDO){//把DO层的数据库属性转换成领域层的属性
         if(Objects.isNull(metaverseRegionDO)){
             return null;
         }
        Region region = new Region();
         region.setId(metaverseRegionDO.getId());
         region.setName(metaverseRegionDO.getName());
         region.setServerLocation(Collections.singletonList(metaverseRegionDO.getServerLocation()));
         return region;
    }

}






















