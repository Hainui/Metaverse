package com.metaverse.region.repository.impl;

import com.alibaba.fastjson.JSONArray;
import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.db.service.IMetaverseRegionService;
import com.metaverse.region.domain.Region;
import com.metaverse.region.repository.MetaverseRegionRepository;
import com.metaverse.user.db.service.IMetaverseUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
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
    public boolean existByName(String name) {
        return regionService.lambdaQuery()
                .eq(MetaverseRegionDO::getName, name)
                .last("LIMIT 1")
                .count() > 0;
    }

    @Override
    public Boolean updateRegionName(Long id, String name, Long currentUserId, Long version) {
        return regionService.lambdaUpdate()
                .eq(MetaverseRegionDO::getId, id)
                .set(MetaverseRegionDO::getName, name)
                .set(MetaverseRegionDO::getVersion, version)
                .set(MetaverseRegionDO::getUpdateBy, currentUserId)
                .update();
    }

    @Override
    public Region findByIdWithLock(Long id) {
        MetaverseRegionDO entity = regionService.lambdaQuery()
                .eq(MetaverseRegionDO::getId, id)
                .last("FOR UPDATE")
                .one();
        return convertFromDO(entity);
    }

    public static Region convertFromDO(MetaverseRegionDO metaverseRegionDO) {//把DO层的数据库属性转换成领域层的属性
        if (Objects.isNull(metaverseRegionDO)) {
            return null;
        }
        Region region = new Region();
        region.setId(metaverseRegionDO.getId());
        region.setName(metaverseRegionDO.getName());
        String serverLocationJson = metaverseRegionDO.getServerLocation();
        List<String> serverLocations = JSONArray.parseArray(serverLocationJson, String.class);
        region.setServerLocation(serverLocations);
        region.setCreateAt(metaverseRegionDO.getCreateAt());
        region.setCreatedBy(metaverseRegionDO.getCreateBy());
        region.setUpdatedAt(metaverseRegionDO.getUpdateAt());
        region.setUpdatedBy(metaverseRegionDO.getUpdateBy());
        region.setVersion(metaverseRegionDO.getVersion());
        return region;
    }
}






















