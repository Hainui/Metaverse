package com.metaverse.user.db.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.metaverse.user.db.entity.MetaverseUserFriendDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户好友关系表 Mapper 接口
 * </p>
 *
 * @author Hainui
 * @since 2024-09-23 12:09:41
 */
@Mapper
public interface MetaverseUserFriendExtMapper extends BaseMapper<MetaverseUserFriendDO> {


    Integer countCommonFriends(Long userId, Long friendUserId);

}