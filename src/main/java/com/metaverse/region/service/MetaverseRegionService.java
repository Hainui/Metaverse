package com.metaverse.region.service;

import com.metaverse.common.Utils.ServerLocationValidator;
import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.db.service.IMetaverseRegionService;
import com.metaverse.region.domain.MetaverseRegion;
import com.metaverse.region.req.ModifyRegionNameReq;
import com.metaverse.region.req.ModifyRegionServerLocationReq;
import com.metaverse.region.req.RegionCreateReq;
import com.metaverse.region.resp.MetaverseRegionResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaverseRegionService {

    private final IMetaverseRegionService regionService;

    public List<MetaverseRegionResp> getAllMetaverseRegion() {
        List<MetaverseRegionDO> list = regionService.lambdaQuery().list();
        return list.stream().map(this::convertToRegionResp).collect(Collectors.toList());
    }

    private MetaverseRegionResp convertToRegionResp(MetaverseRegionDO regionDO) {
        if (Objects.isNull(regionDO)) {
            return null;
        }
        return new MetaverseRegionResp()
                .setId(regionDO.getId())
                .setName(regionDO.getName());
    }

    @Transactional
    public Long create(RegionCreateReq req, Long currentUserId) {
        List<String> serverLocation = req.getServerLocation();
        ServerLocationValidator.validateServerLocations(serverLocation);
        return MetaverseRegion.create(req.getName(), serverLocation, currentUserId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyRegionName(ModifyRegionNameReq req, Long currentUserId) {
        MetaverseRegion region = MetaverseRegion.writeLoadAndAssertNotExist(req.getId());
        return region.modifyRegionName(req.getName(), currentUserId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyRegionLocationList(ModifyRegionServerLocationReq req, Long currentUserId) {
        List<String> serverLocation = req.getServerLocation();
        ServerLocationValidator.validateServerLocations(serverLocation);
        MetaverseRegion region = MetaverseRegion.writeLoadAndAssertNotExist(req.getId());
        return region.modifyRegionLocationList(req.getServerLocation(), currentUserId);
    }
}


