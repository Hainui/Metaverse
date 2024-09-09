package com.metaverse.user.repository.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.metaverse.common.Utils.BCryptUtil;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.permission.db.entity.MetaversePermissionDO;
import com.metaverse.permission.db.entity.MetaverseUserPermissionRelationshipDO;
import com.metaverse.permission.db.service.IMetaversePermissionService;
import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipService;
import com.metaverse.permission.domain.MetaversePermission;
import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.db.service.IMetaverseRegionService;
import com.metaverse.region.domain.MetaverseRegion;
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
import java.util.stream.Collectors;


@RequiredArgsConstructor
@Repository
public class MetaverseUserRepositoryImpl implements MetaverseUserRepository {

    private final IMetaverseUserService userService;
    private final IMetaverseRegionService regionService;
    private final IMetaversePermissionService permissionService;
    private final IMetaverseUserPermissionRelationshipService permissionRelationshipService;

    @Override//查询是否用户名是否存在
    public boolean existByName(String name, Long regionId) {
        return userService.lambdaQuery()
                .eq(MetaverseUserDO::getUsername, name)
                .eq(MetaverseUserDO::getRegionId, regionId)
                .last(RepositoryConstant.LIMIT_ONE)
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
    public MetaverseUser login(String email, String plainTextPassword, Long regionId) {
        List<MetaverseUserDO> list = userService.lambdaQuery()
                .eq(MetaverseUserDO::getRegionId, regionId)
                .list();
        Optional<MetaverseUserDO> userOptional = list.stream().filter(userDO -> StringUtils.equals(email, userDO.getEmail())).findAny();
        if (!userOptional.isPresent()) {
            MetaverseRegionDO regionDO = regionService.lambdaQuery().eq(MetaverseRegionDO::getId, regionId).select(MetaverseRegionDO::getName).one();
            String regionName = regionDO.getName();
            throw new IllegalArgumentException(regionName + "没有此用户信息，请先在" + regionName + "注册");
        } else {
            String storedHashedPassword = userOptional.get().getPassword();
            if (BCryptUtil.checkPassword(plainTextPassword, storedHashedPassword)) {
                return userDOConvertToUser(userOptional.get());
            } else {
                throw new IllegalArgumentException("密码不正确");
            }
        }
    }

    @Override
    public boolean existByRegionId(Long regionId) {
        return regionService.lambdaQuery().eq(MetaverseRegionDO::getId, regionId).exists();
    }

    @Override
    public MetaverseUser findByIdWithLock(Long userId) {
        MetaverseUserDO userDO = userService.lambdaQuery()
                .eq(MetaverseUserDO::getId, userId)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        return userDOConvertToUser(userDO);
    }


    public MetaverseUser userDOConvertToUser(MetaverseUserDO metaverseUserDO) {
        if (Objects.isNull(metaverseUserDO)) {
            return null;
        }
        MetaverseRegionDO regionDO = regionService.lambdaQuery().eq(MetaverseRegionDO::getId, metaverseUserDO.getRegionId()).one();
        List<Long> permissionIds = permissionRelationshipService
                .lambdaQuery()
                .select(MetaverseUserPermissionRelationshipDO::getPermissionId)
                .eq(MetaverseUserPermissionRelationshipDO::getUserId, metaverseUserDO.getId())
                .list()
                .stream()
                .map(MetaverseUserPermissionRelationshipDO::getPermissionId)
                .collect(Collectors.toList());
        List<MetaversePermissionDO> permissionDOList = permissionService.lambdaQuery().in(MetaversePermissionDO::getId, permissionIds).list();
        return new MetaverseUser().
                setId(metaverseUserDO.getId())
                .setEmail(metaverseUserDO.getEmail())
                .setName(metaverseUserDO.getUsername())
                .setPassword(metaverseUserDO.getPassword())
                .setRegion(regionDOConvertToRegion(regionDO))
                .setPermissions(permissionDOList.stream().map(this::permissionDoConvertToPermission).collect(Collectors.toList()))
                .setBirthTime(metaverseUserDO.getBirthTime())
                .setGender(MetaverseUser.Gender.convertGender(metaverseUserDO.getGender()))
                .setUpdatedBy(metaverseUserDO.getUpdateBy())
                .setUpdatedAt(metaverseUserDO.getUpdatedAt())
                .setVersion(metaverseUserDO.getVersion());
    }

    private MetaversePermission permissionDoConvertToPermission(MetaversePermissionDO metaversePermissionDO) {
        if (metaversePermissionDO == null) {
            return null;
        }
        return new MetaversePermission()
                .setId(metaversePermissionDO.getId())
                .setPermissionGroupName(metaversePermissionDO.getPermissionGroupName())
                .setCreateBy(metaversePermissionDO.getCreateBy())
                .setCreateAt(metaversePermissionDO.getCreateAt())
                .setUpdatedAt(metaversePermissionDO.getUpdateAt())
                .setUpdatedBy(metaversePermissionDO.getUpdateBy())
                .setVersion(metaversePermissionDO.getVersion())
                .setPermissions(JSONArray.parseArray(metaversePermissionDO.getPermissions(), String.class));
    }

    private MetaverseRegion regionDOConvertToRegion(MetaverseRegionDO regionDO) {
        if (regionDO == null) {
            return null;
        }
        return new MetaverseRegion()
                .setId(regionDO.getId())
                .setName(regionDO.getName())
                .setServerLocation(JSON.parseArray(regionDO.getServerLocation(), String.class))
                .setCreateAt(regionDO.getCreateAt())
                .setCreatedBy(regionDO.getCreateBy())
                .setUpdatedBy(regionDO.getUpdateBy())
                .setUpdatedAt(regionDO.getUpdateAt())
                .setVersion(regionDO.getVersion());
    }


    @Override
    public boolean modifyUserName(Long userId, String name, Long updateBy, Long newVersion) {
        return userService.lambdaUpdate()
                .eq(MetaverseUserDO::getId, userId)
                .set(MetaverseUserDO::getUsername, name)
                .set(MetaverseUserDO::getUpdateBy, updateBy)
                .set(MetaverseUserDO::getVersion, newVersion)
                .update();
    }

    @Override
    public boolean modifyPassword(String newPassword, Long userId, Long updateBy, Long newVersion) {
        return userService.lambdaUpdate()
                .eq(MetaverseUserDO::getId, userId)
                .set(MetaverseUserDO::getPassword, BCryptUtil.hashPassword(newPassword))
                .set(MetaverseUserDO::getUpdateBy, updateBy)
                .set(MetaverseUserDO::getVersion, newVersion)
                .update();
    }

}






















