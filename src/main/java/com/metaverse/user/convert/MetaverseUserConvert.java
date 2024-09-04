package com.metaverse.user.convert;

import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.domain.MetaverseUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface MetaverseUserConvert {

    MetaverseUserConvert INSTANCE = Mappers.getMapper(MetaverseUserConvert.class);

    MetaverseUser convertToMetaverseUser(MetaverseUserDO entity);
}
