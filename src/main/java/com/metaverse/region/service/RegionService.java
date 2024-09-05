package com.metaverse.region.service;

import com.metaverse.common.Utils.BCryptUtil;
import com.metaverse.common.Utils.BeanManager;
import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.db.service.IMetaverseRegionService;
import com.metaverse.region.domain.Region;
import com.metaverse.region.req.RegionCreateReq;
import com.metaverse.region.req.RegionUpdateReq;
import com.metaverse.region.resp.RegionListResp;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.repository.MetaverseUserRepository;
import com.metaverse.user.req.ModifyUserNameReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegionService {

    private final IMetaverseRegionService regionService;

    public List<RegionListResp> getAllRegion() {
        List<MetaverseRegionDO> list = regionService.lambdaQuery().list();
        return list.stream().map(this::convertToRegionListResp).collect(Collectors.toList());
    }

    public RegionListResp convertToRegionListResp(MetaverseRegionDO regionDO) {
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

    public Long create(RegionCreateReq req,Long currentUserId) {
        return Region.create(req.getName(),req.getServerLocation(),currentUserId);
    }


    public boolean change() {
        return Boolean.TRUE;
    }

    //修改区服名称
    public Boolean updateRegionName(RegionUpdateReq req, Long currentUserId) {
        Boolean region = Region.load(req.getId());//判断id
        return Region.updateRegionName(req,currentUserId);
    }






}


