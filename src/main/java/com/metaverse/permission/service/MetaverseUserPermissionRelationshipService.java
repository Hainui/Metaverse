package com.metaverse.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.metaverse.common.Utils.PermissionComparator;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.permission.MetaverseUserPermissionRelationshipIdGen;
import com.metaverse.permission.db.entity.MetaverseUserPermissionRelationshipDO;
import com.metaverse.permission.db.entity.MetaverseUserPermissionRelationshipDeleteDO;
import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipDeleteService;
import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipService;
import com.metaverse.permission.domain.MetaversePermission;
import com.metaverse.permission.req.AuthoritiesForUserReq;
import com.metaverse.permission.req.AuthoritiesForUsersReq;
import com.metaverse.permission.req.UserAuthoritiesPageReq;
import com.metaverse.permission.resp.UserAuthoritiesPageResp;
import com.metaverse.user.db.mapper.MetaverseUserMapper;
import com.metaverse.user.domain.MetaverseUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaverseUserPermissionRelationshipService {
    private final MetaverseUserPermissionRelationshipIdGen metaverseUserPermissionRelationshipIdGen;
    private final IMetaverseUserPermissionRelationshipService permissionRelationshipService;
    private final IMetaverseUserPermissionRelationshipDeleteService permissionRelationshipDeleteService;//备份 上面删除的信息下面要留作备份
    private final MetaverseUserMapper metaverseUserMapper;

    @Transactional(rollbackFor = Exception.class)
    public Boolean authoritiesImpowerUsers(AuthoritiesForUsersReq req, Long currentUserId) {
        List<MetaverseUser> metaverseUsers = MetaverseUser.readLoadAndAssertNotExist(req.getUserIds());
        List<MetaversePermission> newMetaversePermissions = PermissionComparator.filterIncludedPermissions(MetaversePermission.readLoadAndAssertNotExist(req.getPermissionIds()));
        for (MetaverseUser metaverseUser : metaverseUsers) {
            for (MetaversePermission newMetaversePermission : newMetaversePermissions) {
                List<String> userOldPermissions = metaverseUser.getPermissions().stream().flatMap(Permission -> Permission.getPermissions().stream()).collect(Collectors.toList());
                int code = PermissionComparator.compareLists(userOldPermissions, newMetaversePermission.getPermissions());
                if (code != 1) {
                    permissionRelationshipService
                            .save(new MetaverseUserPermissionRelationshipDO()
                                    .setId(metaverseUserPermissionRelationshipIdGen.nextId())
                                    .setUserId(metaverseUser.getId())
                                    .setPermissionId(newMetaversePermission.getId())
                                    .setImpowerBy(currentUserId)
                                    .setImpowerAt(LocalDateTime.now()));
                }
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean authoritiesResetUsers(AuthoritiesForUsersReq req, Long currentUserId) {
        List<MetaversePermission> newMetaversePermissions = PermissionComparator.filterIncludedPermissions(MetaversePermission.readLoadAndAssertNotExist(req.getPermissionIds()));
        List<Long> userIds = req.getUserIds();
        List<MetaverseUserPermissionRelationshipDO> permissionRelationshipDOs = permissionRelationshipService.lambdaQuery()
                .in(MetaverseUserPermissionRelationshipDO::getUserId, userIds)
                .last(RepositoryConstant.FOR_UPDATE)
                .list();
        permissionRelationshipService.remove(new LambdaQueryWrapper<MetaverseUserPermissionRelationshipDO>().in(MetaverseUserPermissionRelationshipDO::getUserId, userIds));
        permissionRelationshipDOs.forEach(permissionRelationshipDO -> permissionRelationshipDeleteService.save(convertToRelationshipDeleteDo(permissionRelationshipDO, currentUserId, LocalDateTime.now())));
        newMetaversePermissions.forEach(newMetaversePermission -> permissionRelationshipService.save(new MetaverseUserPermissionRelationshipDO()
                .setId(metaverseUserPermissionRelationshipIdGen.nextId())
                .setUserId(newMetaversePermission.getId())
                .setPermissionId(newMetaversePermission.getId())
                .setImpowerAt(LocalDateTime.now())
                .setImpowerBy(currentUserId)));
        return true;

//        MetaversePermission permission = new MetaversePermission();
//        List<MetaverseUser> metaverseUsers = req.getUserIds().stream().map(MetaverseUser::readLoadAndAssertNotExist).collect(Collectors.toList());
//        List<MetaversePermission> newMetaversePermissions = PermissionComparator
//                .filterIncludedPermissions(req//todo filterIncludedPermissions用于过滤权限?
//                        .getPermissionIds()
//                        .stream()
//                        // TODO 目前只是一个查询所以用读锁?
//                        .map(MetaversePermission::readLoadAndAssertNotExist)
//                        .collect(Collectors.toList()));
//        for (MetaverseUser metaverseUser : metaverseUsers) {
//            //删除用户权限
//            permission.deleteUserPermission(metaverseUser);
//            for (MetaversePermission metaversePermission : newMetaversePermissions) {
//                //备份用户权限
//                permissionRelationshipDeleteService.save(new MetaverseUserPermissionRelationshipDeleteDO()
//                        .setUserId(metaverseUser.getId())
//                        .setPermissionId(metaversePermission.getId())
//                        .setId(metaverseUserPermissionRelationshipIdGen.nextId())
//                        .setImpowerBy(currentUserId)
//                        .setImpowerAt(LocalDateTime.now()));
//                //插入权限
//                permissionRelationshipService
//                        .save(new MetaverseUserPermissionRelationshipDO()
//                                .setUserId(metaverseUser.getId())
//                                .setPermissionId(metaversePermission.getId())
//                                .setId(metaverseUserPermissionRelationshipIdGen.nextId())
//                                .setImpowerBy(currentUserId)
//                                .setImpowerAt(LocalDateTime.now()));
//            }
//        }
//        return true;
    }

    private MetaverseUserPermissionRelationshipDeleteDO convertToRelationshipDeleteDo(MetaverseUserPermissionRelationshipDO permissionRelationshipDO, Long currentUserId, LocalDateTime now) {
        if (Objects.isNull(permissionRelationshipDO)) {
            return null;
        }
        return new MetaverseUserPermissionRelationshipDeleteDO()
                .setId(permissionRelationshipDO.getId())
                .setUserId(permissionRelationshipDO.getId())
                .setPermissionId(permissionRelationshipDO.getId())
                .setImpowerAt(permissionRelationshipDO.getImpowerAt())
                .setImpowerBy(permissionRelationshipDO.getImpowerBy())
                .setDeleteBy(currentUserId)
                .setDeleteAt(now);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean authoritiesRevokeForUsers(AuthoritiesForUsersReq req, Long currentUserId) {
        List<Long> deletePermissionIds = req.getPermissionIds();

        Map<Long, List<MetaverseUserPermissionRelationshipDO>> permissionRelationshipDOs = permissionRelationshipService.lambdaQuery()
                .in(MetaverseUserPermissionRelationshipDO::getUserId, req.getUserIds())
                .last(RepositoryConstant.FOR_UPDATE)
                .list()
                .stream()
                .collect(Collectors.groupingBy(MetaverseUserPermissionRelationshipDO::getUserId));

        for (Map.Entry<Long, List<MetaverseUserPermissionRelationshipDO>> map : permissionRelationshipDOs.entrySet()) {
            Long userId = map.getKey();
            List<MetaverseUserPermissionRelationshipDO> dos = map.getValue();
            for (MetaverseUserPermissionRelationshipDO oldPermissionRelationshipDO : dos) {
                Long oldPermissionId = oldPermissionRelationshipDO.getPermissionId();
                if (deletePermissionIds.contains(oldPermissionId)) {
                    permissionRelationshipService.remove(new LambdaQueryWrapper<MetaverseUserPermissionRelationshipDO>()
                            .eq(MetaverseUserPermissionRelationshipDO::getUserId, userId)
                            .eq(MetaverseUserPermissionRelationshipDO::getPermissionId, oldPermissionId));
                    permissionRelationshipDeleteService.save(convertToRelationshipDeleteDo(oldPermissionRelationshipDO, currentUserId, LocalDateTime.now()));
                }
            }
        }


//        MetaversePermission permission = new MetaversePermission();
//        List<MetaverseUser> metaverseUsers = req.getUserIds().stream().map(MetaverseUser::readLoadAndAssertNotExist).collect(Collectors.toList());
//        List<MetaversePermission> newMetaversePermissions = PermissionComparator
//                .filterIncludedPermissions(req
//                        .getPermissionIds()
//                        .stream()
//                        .map(MetaversePermission::readLoadAndAssertNotExist)
//                        .collect(Collectors.toList()));
//        for (MetaverseUser metaverseUser : metaverseUsers) {
//            for (MetaversePermission newMetaversePermission : newMetaversePermissions) {
//                //删除选中的权限
//                List<String> userOldPermissions = metaverseUser.getPermissions().stream().flatMap(Permission -> Permission.getPermissions().stream()).collect(Collectors.toList());
//                int code = PermissionComparator.compareLists(userOldPermissions, newMetaversePermission.getPermissions());
//                if (code != 0) {
//                    permission.deleteUserPermissionId(metaverseUser.getId(), newMetaversePermission.getId());
//                    permissionRelationshipDeleteService.save(new MetaverseUserPermissionRelationshipDeleteDO()
//                            .setUserId(metaverseUser.getId())
//                            .setPermissionId(newMetaversePermission.getId())
//                            .setId(metaverseUserPermissionRelationshipIdGen.nextId())
//                            .setImpowerBy(currentUserId)
//                            .setImpowerAt(LocalDateTime.now()));
//                }
//            }
//        }
//

        return true;
    }

    public Boolean authoritiesRevokeForUser(AuthoritiesForUserReq req, Long currentUserId) {
        List<Long> deletePermissionIds = req.getPermissionIds();
        List<MetaverseUserPermissionRelationshipDO> relationshipDOs = permissionRelationshipService.lambdaQuery()
                .eq(MetaverseUserPermissionRelationshipDO::getUserId, req.getUserId())
                .last(RepositoryConstant.FOR_UPDATE)
                .list();
        for (MetaverseUserPermissionRelationshipDO relationshipDO : relationshipDOs) {
            Long oldPermissionId = relationshipDO.getPermissionId();
            if (deletePermissionIds.contains(oldPermissionId)) {
                permissionRelationshipService.remove(new LambdaQueryWrapper<MetaverseUserPermissionRelationshipDO>()
                        .eq(MetaverseUserPermissionRelationshipDO::getUserId, req.getUserId())
                        .eq(MetaverseUserPermissionRelationshipDO::getPermissionId, oldPermissionId));
                permissionRelationshipDeleteService.save(convertToRelationshipDeleteDo(relationshipDO, currentUserId, LocalDateTime.now()));
            }
        }

//        MetaversePermission permission = new MetaversePermission();
//        MetaversePermission.writeLoadAndAssertNotExist(req.getUserId());
//        return permission.authoritiesRevokeForUser(req.getUserId(), req.getPermissionIds(), currentUserId);
        return true;
    }

    public Boolean authoritiesImpowerUser(AuthoritiesForUserReq req, Long currentUserId) {
        MetaverseUser metaverseUser = MetaverseUser.readLoadAndAssertNotExist(req.getUserId());
        List<MetaversePermission> newMetaversePermissions = PermissionComparator.filterIncludedPermissions(MetaversePermission.readLoadAndAssertNotExist(req.getPermissionIds()));
        List<String> userOldPermissions = metaverseUser.getPermissions().stream().flatMap(Permission -> Permission.getPermissions().stream()).collect(Collectors.toList());
        for (MetaversePermission newMetaversePermission : newMetaversePermissions) {
            int code = PermissionComparator.compareLists(userOldPermissions, newMetaversePermission.getPermissions());
            if (code != 1) {
                permissionRelationshipService
                        .save(new MetaverseUserPermissionRelationshipDO()
                                .setId(metaverseUserPermissionRelationshipIdGen.nextId())
                                .setUserId(metaverseUser.getId())
                                .setPermissionId(newMetaversePermission.getId())
                                .setImpowerBy(currentUserId)
                                .setImpowerAt(LocalDateTime.now()));
            }
        }
        return null;
    }


    public UserAuthoritiesPageResp userAuthoritiesPageView(UserAuthoritiesPageReq req) {
        

        return null;
    }


}
