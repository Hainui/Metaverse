package com.metaverse.permission.domain;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.metaverse.common.Utils.BeanManager;
import com.metaverse.common.model.IEntity;
import com.metaverse.permission.MetaversePermissionIdGen;
import com.metaverse.permission.db.entity.MetaversePermissionDO;
import com.metaverse.permission.db.entity.MetaverseUserPermissionRelationshipDeleteDO;
import com.metaverse.permission.repository.MetaversePermissionRepository;
import com.metaverse.user.domain.MetaverseUser;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class MetaversePermission implements IEntity {

    protected static final Long MODEL_VERSION = 1L;
    /**
     * 权限ID
     */
    private Long id;
    /**
     * 权限组名
     */
    private String permissionGroupName;
    /**
     * 权限串集合
     */
    private List<String> permissions;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 创建时间
     */
    private LocalDateTime createAt;
    /**
     * 最近一次修改时间
     */
    private LocalDateTime updatedAt;
    /**
     * 修改人
     */
    private Long updatedBy;
    /**
     * 版本号，每次变更+1
     */
    private Long version;


    public static Long create(String name, List<String> permissions, Long currentUserId) {
        MetaversePermissionIdGen idGen = BeanManager.getBean(MetaversePermissionIdGen.class);
        MetaversePermissionRepository repository = BeanManager.getBean(MetaversePermissionRepository.class);
        MetaversePermissionDO metaversePermissionDO = new MetaversePermissionDO()
                .setId(idGen.nextId())
                .setPermissionGroupName(name)
                .setPermissions(JSON.toJSONString(permissions))
                .setCreateBy(currentUserId)
                .setCreateAt(LocalDateTime.now());
        if (!repository.save(metaversePermissionDO, permissions)) {
            throw new IllegalArgumentException("权限新建失败");
        }
        return metaversePermissionDO.getId();
    }

    public static MetaversePermission writeLoadAndAssertNotExist(Long id) {//写锁
        MetaversePermissionRepository repository = BeanManager.getBean(MetaversePermissionRepository.class);
        MetaversePermission permission = repository.findByIdWithWriteLock(id);
        if (Objects.isNull(permission)) {
            throw new IllegalArgumentException("未找到该权限信息");
        }
        return permission;
    }

    public static MetaversePermission readLoadAndAssertNotExist(Long id) {//读锁
        MetaversePermissionRepository repository = BeanManager.getBean(MetaversePermissionRepository.class);
        MetaversePermission permission = repository.findByIdWithReadLock(id);
        if (Objects.isNull(permission)) {
            throw new IllegalArgumentException("未找到该权限信息");
        }
        return permission;
    }

    public static List<MetaversePermission> readLoadAndAssertNotExist(List<Long> ids) {//读锁
        MetaversePermissionRepository repository = BeanManager.getBean(MetaversePermissionRepository.class);
        List<MetaversePermission> permissions = repository.findByIdsWithReadLock(ids);
        if (CollectionUtil.isNotEmpty(permissions)) {
            throw new IllegalArgumentException("未找到该权限信息");
        }
        return permissions;
    }


    public Boolean modifyPermissionName(String name, Long currentUserId) {
        if (StringUtils.equals(this.permissionGroupName, name)) {
            throw new IllegalArgumentException("新的权限名称不能和旧权限名称相同");
        }
        MetaversePermissionRepository repository = BeanManager.getBean(MetaversePermissionRepository.class);
        if (repository.existByName(name)) {
            throw new IllegalArgumentException("该权限名称已存在");
        }
        Long newVersion = changeVersion();
        return repository.updatePermissionName(pkVal(), name, currentUserId, newVersion);
    }

    public Boolean modifyPermissions(List<String> permissions, Long currentUserId) {
        if (Objects.equals(this.permissions, permissions)) {
            throw new IllegalArgumentException("新权限串集合和旧权限串集合相同,无需修改");
        }
        MetaversePermissionRepository repository = BeanManager.getBean(MetaversePermissionRepository.class);
        Long newVersion = changeVersion();
        return repository.modifyPermissions(permissions, pkVal(), currentUserId, newVersion, this.permissions);
    }

    public Boolean authoritiesResetUsers(List<Long> permissionIds, List<Long> userIds, Long currentUserId) {
        MetaversePermissionRepository repository = BeanManager.getBean(MetaversePermissionRepository.class);

        // 删除现有权限关系
        for (Long userId : userIds) {
            repository.deleteAllUserIdPermission(userId);
            //todo 备份
            for (Long permissionId : permissionIds) {
                MetaverseUserPermissionRelationshipDeleteDO metaverseUserPermissionRelationshipDeleteDO = new MetaverseUserPermissionRelationshipDeleteDO()
                        .setUserId(userId)
                        .setPermissionId(permissionId)
                        .setDeleteBy(currentUserId)
                        .setDeleteAt(LocalDateTime.now());
                if (!repository.backupDeleteAllUserIdPermission(metaverseUserPermissionRelationshipDeleteDO)) {
                    throw new IllegalArgumentException("权限备份失败");
                }
            }
        }
        // 插入新的权限关系
        if (!repository.saveUserPermission(permissionIds, userIds, currentUserId)) {
            throw new IllegalArgumentException("权限关系重置失败");
        }
        return true;
    }

    public Boolean authoritiesRevokeForUsers(List<Long> userIds, List<Long> permissionIds, Long currentUserId) {
        MetaversePermissionRepository repository = BeanManager.getBean(MetaversePermissionRepository.class);
        if (!repository.deleteUserIdPermission(userIds, permissionIds)) {
            throw new IllegalArgumentException("权限关系删除失败");
        } else {
            // todo 没有删除失败就需要备份
            for (Long userId : userIds) {
                for (Long permissionId : permissionIds) {
                    MetaverseUserPermissionRelationshipDeleteDO metaverseUserPermissionRelationshipDeleteDO = new MetaverseUserPermissionRelationshipDeleteDO()
                            .setUserId(userId)
                            .setPermissionId(permissionId)
                            .setDeleteBy(currentUserId)
                            .setDeleteAt(LocalDateTime.now());
                    if (!repository.backupDeleteAllUserIdPermission(metaverseUserPermissionRelationshipDeleteDO)) {
                        throw new IllegalArgumentException("权限备份失败");
                    }
                }
            }
        }
        return true;
    }

    public Boolean authoritiesRevokeForUser(Long userId, List<Long> permissionIds, Long currentUserId) {
        MetaversePermissionRepository repository = BeanManager.getBean(MetaversePermissionRepository.class);
        if (!repository.deleteOneUserPermission(userId, permissionIds)) {
            throw new IllegalArgumentException("权限删除失败");
        } else {
            for (Long permissionId : permissionIds) {
                MetaverseUserPermissionRelationshipDeleteDO metaverseUserPermissionRelationshipDeleteDO = new MetaverseUserPermissionRelationshipDeleteDO()
                        .setUserId(userId)
                        .setPermissionId(permissionId)
                        .setDeleteBy(currentUserId)
                        .setDeleteAt(LocalDateTime.now());
                if (!repository.backupDeleteAllUserIdPermission(metaverseUserPermissionRelationshipDeleteDO)) {
                    throw new IllegalArgumentException("权限备份失败");
                }
            }
        }

        return true;
    }

    @Override
    public Long pkVal() {
        return id;
    }

    @Override
    public Long modelVersion() {
        return MODEL_VERSION;
    }

    @Override
    public Long changeVersion() {
        return ++version;
    }


    public void deleteUserPermission(MetaverseUser metaverseUser) {
        MetaversePermissionRepository repository = BeanManager.getBean(MetaversePermissionRepository.class);
        if (!repository.deleteAllUserIdPermission(metaverseUser.getId())) {
            throw new IllegalArgumentException("权限删除失败");
        }
    }


    public void deleteUserPermissionId(Long UserId, Long permissionsId) {
        MetaversePermissionRepository repository = BeanManager.getBean(MetaversePermissionRepository.class);
        if (!repository.deleteUserPermissionId(UserId, permissionsId)) {
            throw new IllegalArgumentException("权限删除失败");
        }
    }
}
