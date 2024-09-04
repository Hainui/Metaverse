package com.metaverse.region.convert;


import com.metaverse.region.db.entity.RegionDO;
import com.metaverse.region.resp.RegionListResp;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface RegionCovert {
    RegionCovert INSTANCE = Mappers.getMapper(RegionCovert.class);

    RegionListResp convertToRegionResp(RegionDO regionDO);
}
