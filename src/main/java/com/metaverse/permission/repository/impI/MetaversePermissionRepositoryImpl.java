package com.metaverse.permission.repository.impI;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.permission.db.entity.*;
import com.metaverse.permission.db.service.*;
import com.metaverse.permission.domain.MetaversePermission;
import com.metaverse.permission.repository.MetaversePermissionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Repository
@Slf4j
public class MetaversePermissionRepositoryImpl implements MetaversePermissionRepository {
    private final IMetaversePermissionService permissionService;//用户权限
    private final IMetaverseResourceTypeEnumService resourceTypeEnumService;//资源
    private final IMetaverseActionEnumService actionEnumService;//动作
    private final IMetaverseLocatorEnumService locatorEnumService;//定位
    private final IMetaverseUserPermissionRelationshipService userPermissionService;//用户关联

    private final IMetaverseUserPermissionRelationshipDeleteService permissionRelationshipDeleteService;//备份 上面删除的信息下面要留作备份

    @Override
    public boolean save(MetaversePermissionDO metaversePermissionDO, List<String> permissions) {
        savePermissionConstantPool(permissions);
        return permissionService.save(metaversePermissionDO);
    }

    // 快照读
    @Override
    public boolean existByName(String name) {
        return permissionService.lambdaQuery()
                .eq(MetaversePermissionDO::getPermissionGroupName, name)
                .last(RepositoryConstant.LIMIT_ONE)
                .count() > 0;
    }

    @Override
    public boolean updatePermissionName(Long id, String name, Long currentUserId, Long version) {
        return permissionService.lambdaUpdate()
                .eq(MetaversePermissionDO::getId, id)
                .set(MetaversePermissionDO::getPermissionGroupName, name)
                .set(MetaversePermissionDO::getUpdateBy, currentUserId)
                .set(MetaversePermissionDO::getVersion, version)
                .update();
    }

    // 排它锁
    @Override
    public MetaversePermission findByIdWithWriteLock(Long id) {
        MetaversePermissionDO permission = permissionService.lambdaQuery()
                .eq(MetaversePermissionDO::getId, id)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        return covertFromDo(permission);
    }

    // 共享锁
    @Override
    public MetaversePermission findByIdWithReadLock(Long id) {
        MetaversePermissionDO entity = permissionService.lambdaQuery()
                .eq(MetaversePermissionDO::getId, id)
                .last(RepositoryConstant.FOR_SHARE)
                .one();
        return covertFromDo(entity);
    }


    public static MetaversePermission covertFromDo(MetaversePermissionDO metaversePermissionDO) {
        if (Objects.isNull(metaversePermissionDO)) {
            return null;
        }
        MetaversePermission permission = new MetaversePermission();
        permission.setId(metaversePermissionDO.getId());
        permission.setPermissionGroupName(metaversePermissionDO.getPermissionGroupName());

        String permissions = metaversePermissionDO.getPermissions();
        permission.setPermissions(JSONArray.parseArray(permissions, String.class));
        permission.setCreateBy(metaversePermissionDO.getCreateBy());
        permission.setCreateAt(metaversePermissionDO.getCreateAt());
        permission.setUpdatedBy(metaversePermissionDO.getUpdateBy());
        permission.setUpdatedAt(metaversePermissionDO.getUpdateAt());
        permission.setVersion(metaversePermissionDO.getVersion());
        return permission;
    }

    @Override
    public boolean modifyPermissions(List<String> newPermissions, Long id, Long currentUserId, Long newVersion, List<String> oldPermissions) {
        boolean updated = permissionService.lambdaUpdate()
                .eq(MetaversePermissionDO::getId, id)
                .set(MetaversePermissionDO::getPermissions, JSON.toJSONString(newPermissions))
                .set(MetaversePermissionDO::getUpdateBy, currentUserId)
                .set(MetaversePermissionDO::getVersion, newVersion)
                .update();

        List<String> permissions = permissionService
                .lambdaQuery()
                .select(MetaversePermissionDO::getPermissions)
                .last(RepositoryConstant.FOR_SHARE)
                .list()
                .parallelStream() // 使用 parallelStream 而不是 stream
                .map(MetaversePermissionDO::getPermissions)
                .flatMap(permissionJson -> JSONArray.parseArray(permissionJson, String.class).stream())
                .collect(Collectors.toList());

        for (String oldPermission : oldPermissions) { // 执行权限串常量池删除逻辑
            String[] permissionStr = oldPermission.split("\\.");
            String resourceType = permissionStr[0];
            String action = permissionStr[1];
            String locator = permissionStr[2];

            boolean startsWithResourceType = permissions.stream().anyMatch(permission -> permission.startsWith(resourceType + "."));
            if (!startsWithResourceType) {
                resourceTypeEnumService.remove(new LambdaQueryWrapper<MetaverseResourceTypeEnumDO>().eq(MetaverseResourceTypeEnumDO::getResourceType, resourceType));
            }

            boolean containsAction = permissions.stream().anyMatch(permission -> permission.contains("." + action + "."));
            if (!containsAction) {
                actionEnumService.remove(new LambdaQueryWrapper<MetaverseActionEnumDO>().eq(MetaverseActionEnumDO::getAction, action));
            }

            boolean endsWithLocator = permissions.stream().anyMatch(permission -> permission.endsWith("." + locator));
            if (!endsWithLocator) {
                locatorEnumService.remove(new LambdaQueryWrapper<MetaverseLocatorEnumDO>().eq(MetaverseLocatorEnumDO::getLocator, locator));
            }
        }
        // 执行权限串常量池新增逻辑
        savePermissionConstantPool(newPermissions);
        return updated;
    }

    private void savePermissionConstantPool(List<String> newPermissions) {
        for (String newPermission : newPermissions) {
            String[] permissionStr = newPermission.split("\\.");
            String resourceType = permissionStr[0];
            String action = permissionStr[1];
            String locator = permissionStr[2];

            try {
                resourceTypeEnumService.save(new MetaverseResourceTypeEnumDO().setResourceType(resourceType));
            } catch (DuplicateKeyException e) {
                // 处理唯一键冲突，可以选择忽略或记录错误
                log.info("资源类型 {} 已经存在，跳过插入。", resourceType);
            }

            try {
                actionEnumService.save(new MetaverseActionEnumDO().setAction(action));
            } catch (DuplicateKeyException e) {
                // 处理唯一键冲突，可以选择忽略或记录错误
                log.info("动作 {} 已经存在，跳过插入。", action);
            }

            try {
                locatorEnumService.save(new MetaverseLocatorEnumDO().setLocator(locator));
            } catch (DuplicateKeyException e) {
                // 处理唯一键冲突，可以选择忽略或记录错误
                log.info("定位器 {} 已经存在，跳过插入。", locator);
            }
        }
    }

    @Override
    public boolean deleteAllUserIdPermission(Long userId) {
        //todo 删除之后要备份
        QueryWrapper<MetaverseUserPermissionRelationshipDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userId);
        return userPermissionService.remove(queryWrapper);
    }

    @Override
    public boolean backupDeleteAllUserIdPermission(MetaverseUserPermissionRelationshipDeleteDO metaverseUserPermissionRelationshipDeleteDO) {
        return permissionRelationshipDeleteService.save(metaverseUserPermissionRelationshipDeleteDO);
    }

    @Override
    public boolean saveUserPermission(List<Long> permissionIds, List<Long> userIds, Long currentUserId) {
        List<MetaverseUserPermissionRelationshipDO> newRelationships = new ArrayList<>();
        for (Long userId : userIds) {
            for (Long permissionId : permissionIds) {
                MetaverseUserPermissionRelationshipDO userPermission = new MetaverseUserPermissionRelationshipDO();
                userPermission.setUserId(userId);
                userPermission.setPermissionId(permissionId);
                userPermission.setImpowerBy(currentUserId);
                userPermission.setImpowerAt(LocalDateTime.now());
                newRelationships.add(userPermission);
            }
        }
        try {
            //saveBatch批量插入实体到数据库
            return userPermissionService.saveBatch(newRelationships);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean deleteUserIdPermission(List<Long> userId, List<Long> permissionIds) {
        QueryWrapper<MetaverseUserPermissionRelationshipDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_id", userId);
        queryWrapper.in("permission_id", permissionIds);
        return userPermissionService.remove(queryWrapper);
    }

    @Override
    public boolean deleteOneUserPermission(Long userId, List<Long> permissionIds) {
        QueryWrapper<MetaverseUserPermissionRelationshipDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.in("permission_id", permissionIds);
        return userPermissionService.remove(queryWrapper);
    }


}
