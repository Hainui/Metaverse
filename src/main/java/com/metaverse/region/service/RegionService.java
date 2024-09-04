package com.metaverse.region.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.metaverse.region.db.entity.RegionDO;
import com.metaverse.region.db.mapper.RegionMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class RegionService {



    private final RegionMapper regionMapper;

    @Transactional
    public List<String> getAllRegionNames() {
        QueryWrapper<RegionDO> queryWrapper = new QueryWrapper<>();
        List<RegionDO> regions = regionMapper.selectList(queryWrapper);
        return regions.stream().map(RegionDO::getName).toList();
    }

    public Long create() {
        return 111L;
    }


    public boolean change() {
        return Boolean.TRUE;
    }
}
