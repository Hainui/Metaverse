package com.metaverse.region.service;

import com.metaverse.region.db.entity.RegionDO;
import com.metaverse.region.db.service.IRegionService;
import com.metaverse.region.resp.RegionListResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {

    private final IRegionService iRegionService;

    public List<RegionListResp> getAllRegion() {
        List<RegionDO> list = iRegionService.lambdaQuery().list();
        return list.stream().map(this::convertToRegionListResp).collect(Collectors.toList());
    }

    public RegionListResp convertToRegionListResp(RegionDO regionDO) {
        if (regionDO == null) {
            return null;
        }
        RegionListResp resp = new RegionListResp();
        if (regionDO.getId() != null) {
            resp.setId(regionDO.getId());
        }
        if (regionDO.getName() != null) {
            resp.setName(regionDO.getName());
        }
        return resp;
    }

    public Long create() {
        return 111L;
    }


    public boolean change() {
        return Boolean.TRUE;
    }
}


