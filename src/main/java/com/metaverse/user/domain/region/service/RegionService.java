package com.metaverse.user.domain.region.service;

import com.metaverse.user.domain.region.db.entity.MetaverseRegionDO;
import com.metaverse.user.domain.region.db.service.IMetaverseRegionService;
import com.metaverse.user.domain.region.domain.MetaverseRegion;
import com.metaverse.user.domain.region.req.ModifyRegionReq;
import com.metaverse.user.domain.region.req.RegionCreateReq;
import com.metaverse.user.domain.region.resp.RegionResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {

    private final IMetaverseRegionService regionService;

    public List<RegionResp> getAllRegion() {
        List<MetaverseRegionDO> list = regionService.lambdaQuery().list();
        return list.stream().map(this::convertToRegionListResp).collect(Collectors.toList());
    }

    public RegionResp convertToRegionListResp(MetaverseRegionDO regionDO) {
        if (regionDO == null) {
            return null;
        }
        RegionResp resp = new RegionResp();
        if (regionDO.getId() != null) {
            resp.setId(regionDO.getId());
        }
        if (regionDO.getName() != null) {
            resp.setName(regionDO.getName());
        }
        return resp;
    }

    public Long create(RegionCreateReq req, Long currentUserId) {
        // todo 权限校验
        return MetaverseRegion.create(req.getName(), req.getServerLocation(), currentUserId);
    }

    /**
     * 修改区服名称
     */
    public Boolean modifyRegionName(ModifyRegionReq req, Long currentUserId) {
        // todo 权限校验
        MetaverseRegion region = MetaverseRegion.loadAndAssertNotExist(req.getId());
        return region.modifyRegionName(req, currentUserId);
    }


}


