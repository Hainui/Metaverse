package com.metaverse.user.repository.impl;

import com.metaverse.common.Utils.BCryptUtil;
import com.metaverse.region.db.entity.RegionDO;
import com.metaverse.region.db.service.IRegionService;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.db.service.IMetaverseUserService;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.repository.MetaverseUserRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;


@RequiredArgsConstructor
@Repository
public class MetaverseUserRepositoryImpl implements MetaverseUserRepository {

    private final IMetaverseUserService userService;
    private final IRegionService iRegionService;

    @Override//查询是否用户名是否存在
    public boolean existByName(String name, Long regionId) {
        return userService.lambdaQuery()
                .eq(MetaverseUserDO::getUsername, name)
                .eq(MetaverseUserDO::getRegionId, regionId)
                .last("LIMIT 1")
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

    @Override
    public Long login(String email, String plainTextPassword, Long regionId) {
        List<MetaverseUserDO> list = userService.lambdaQuery()
                .eq(MetaverseUserDO::getRegionId, regionId)
                .list();
        Optional<MetaverseUserDO> userOptional = list.stream().filter(userDO -> StringUtils.equals(email, userDO.getEmail())).findAny();
        if (!userOptional.isPresent()) {
            RegionDO regionDO = iRegionService.lambdaQuery().eq(RegionDO::getId, regionId).select(RegionDO::getName).one();
            String regionName = regionDO.getName();
            throw new IllegalArgumentException(regionName + "没有此用户信息，请先在" + regionName + "注册");
        } else {
            String storedHashedPassword = userOptional.get().getPassword();
            if (BCryptUtil.checkPassword(plainTextPassword, storedHashedPassword)) {
                return userOptional.get().getId();
            } else {
                throw new IllegalArgumentException("密码不正确");
            }
        }
    }

    @Override
    public boolean existByRegionId(Long regionId) {
        return iRegionService.lambdaQuery().eq(RegionDO::getId, regionId).exists();
    }

    @Override
    public MetaverseUser findByIdWithLock(Long userId) {
        MetaverseUserDO entity = userService.lambdaQuery()
                .eq(MetaverseUserDO::getId, userId)
                .last("FOR UPDATE")
                .one();
        return convertFromDO(entity);
    }


    public static MetaverseUser convertFromDO(MetaverseUserDO metaverseUserDO) {
        if (Objects.isNull(metaverseUserDO)) {
            return null;
        }

        MetaverseUser metaverseUser = new MetaverseUser();
        metaverseUser.setId(metaverseUserDO.getId());
        metaverseUser.setEmail(metaverseUserDO.getEmail());
        metaverseUser.setName(metaverseUserDO.getUsername()); // 注意：这里使用 username 替换 name
        metaverseUser.setPassword(metaverseUserDO.getPassword());
        metaverseUser.setRegionId(metaverseUserDO.getRegionId());
        metaverseUser.setBirthTime(metaverseUserDO.getBirthTime());
        metaverseUser.setGender(MetaverseUser.Gender.convertGender(metaverseUserDO.getGender()));

        return metaverseUser;
    }


    @Override
    public boolean modifyUserName(Long userId, String name) {
        return userService.lambdaUpdate().eq(MetaverseUserDO::getId, userId).set(MetaverseUserDO::getUsername, name).update();
    }
}






















