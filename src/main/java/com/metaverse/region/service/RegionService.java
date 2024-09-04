package com.metaverse.region.service;

import com.metaverse.region.convert.RegionCovert;
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
        return list.stream().map(RegionCovert.INSTANCE::convertToRegionResp).collect(Collectors.toList());
    }

    public Long create() {
        return 111L;
    }


    public boolean change() {
        return Boolean.TRUE;
    }
}


