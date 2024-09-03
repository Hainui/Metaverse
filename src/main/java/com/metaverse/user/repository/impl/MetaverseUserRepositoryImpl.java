package com.metaverse.user.repository.impl;

import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.db.service.IMetaverseUserService;
import com.metaverse.user.repository.MetaverseUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;


@RequiredArgsConstructor
@Repository
public class MetaverseUserRepositoryImpl implements MetaverseUserRepository {

    private final IMetaverseUserService userService;

    @Override
    public boolean existByName(String name, Long regionId) {
        return userService.lambdaQuery()
                .eq(MetaverseUserDO::getUsername, name)
                .eq(MetaverseUserDO::getRegionId, regionId)
                .last("LIMT 1")
                .count() > 0;
    }

    @Override
    public boolean save(MetaverseUserDO userDO) {
        try {
            return userService.save(userDO);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("存在重复的数据！");
        }
    }
}
