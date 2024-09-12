package com.metaverse.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.metaverse.common.Utils.PermissionComparator;
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
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.db.mapper.MetaverseUserMapper;
import com.metaverse.user.domain.MetaverseUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
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
        List<MetaverseUser> metaverseUsers = req.getUserIds().stream().map(MetaverseUser::readLoadAndAssertNotExist).collect(Collectors.toList());
        List<MetaversePermission> newMetaversePermissions = PermissionComparator
                .filterIncludedPermissions(req
                        .getPermissionIds()
                        .stream()
                        .map(MetaversePermission::readLoadAndAssertNotExist)
                        .collect(Collectors.toList()));
        for (MetaverseUser metaverseUser : metaverseUsers) {
            for (MetaversePermission newMetaversePermission : newMetaversePermissions) {
                List<String> userOldPermissions = metaverseUser.getPermissions().stream().flatMap(Permission -> Permission.getPermissions().stream()).collect(Collectors.toList());
                int code = PermissionComparator.compareLists(userOldPermissions, newMetaversePermission.getPermissions());
                if (code != 1) {
                    permissionRelationshipService
                            .save(new MetaverseUserPermissionRelationshipDO()
                                    .setUserId(metaverseUser.getId())
                                    .setPermissionId(newMetaversePermission.getId())
                                    .setId(metaverseUserPermissionRelationshipIdGen.nextId())
                                    .setImpowerBy(currentUserId)
                                    .setImpowerAt(LocalDateTime.now()));
                }
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean authoritiesResetUsers(AuthoritiesForUsersReq req, Long currentUserId) {
        MetaversePermission permission = new MetaversePermission();
        List<MetaverseUser> metaverseUsers = req.getUserIds().stream().map(MetaverseUser::readLoadAndAssertNotExist).collect(Collectors.toList());
        List<MetaversePermission> newMetaversePermissions = PermissionComparator
                .filterIncludedPermissions(req//todo filterIncludedPermissions用于过滤权限?
                        .getPermissionIds()
                        .stream()
                        // TODO 目前只是一个查询所以用读锁?
                        .map(MetaversePermission::readLoadAndAssertNotExist)
                        .collect(Collectors.toList()));
        for (MetaverseUser metaverseUser : metaverseUsers) {
            //删除用户权限
            permission.deleteUserPermission(metaverseUser);
            for (MetaversePermission metaversePermission : newMetaversePermissions) {
                //备份用户权限
                permissionRelationshipDeleteService.save(new MetaverseUserPermissionRelationshipDeleteDO()
                        .setUserId(metaverseUser.getId())
                        .setPermissionId(metaversePermission.getId())
                        .setId(metaverseUserPermissionRelationshipIdGen.nextId())
                        .setImpowerBy(currentUserId)
                        .setImpowerAt(LocalDateTime.now()));
                //插入权限
                permissionRelationshipService
                        .save(new MetaverseUserPermissionRelationshipDO()
                                .setUserId(metaverseUser.getId())
                                .setPermissionId(metaversePermission.getId())
                                .setId(metaverseUserPermissionRelationshipIdGen.nextId())
                                .setImpowerBy(currentUserId)
                                .setImpowerAt(LocalDateTime.now()));
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean authoritiesRevokeForUsers(AuthoritiesForUsersReq req, Long currentUserId) {
        MetaversePermission permission = new MetaversePermission();
        List<MetaverseUser> metaverseUsers = req.getUserIds().stream().map(MetaverseUser::readLoadAndAssertNotExist).collect(Collectors.toList());
        List<MetaversePermission> newMetaversePermissions = PermissionComparator
                .filterIncludedPermissions(req
                        .getPermissionIds()
                        .stream()
                        .map(MetaversePermission::readLoadAndAssertNotExist)
                        .collect(Collectors.toList()));
        for (MetaverseUser metaverseUser : metaverseUsers) {
            for (MetaversePermission newMetaversePermission : newMetaversePermissions) {
                //删除选中的权限
                List<String> userOldPermissions = metaverseUser.getPermissions().stream().flatMap(Permission -> Permission.getPermissions().stream()).collect(Collectors.toList());
                int code = PermissionComparator.compareLists(userOldPermissions, newMetaversePermission.getPermissions());
                if (code != 0) {
                    permission.deleteUserPermissionId(metaverseUser.getId(), newMetaversePermission.getId());
                    permissionRelationshipDeleteService.save(new MetaverseUserPermissionRelationshipDeleteDO()
                            .setUserId(metaverseUser.getId())
                            .setPermissionId(newMetaversePermission.getId())
                            .setId(metaverseUserPermissionRelationshipIdGen.nextId())
                            .setImpowerBy(currentUserId)
                            .setImpowerAt(LocalDateTime.now()));
                }
            }
        }


        return true;
    }

    public Boolean authoritiesRevokeForUser(AuthoritiesForUserReq req, Long currentUserId) {
        MetaversePermission permission = new MetaversePermission();
        MetaversePermission.writeLoadAndAssertNotExist(req.getUserId());
        return permission.authoritiesRevokeForUser(req.getUserId(), req.getPermissionIds(), currentUserId);
    }

    public Boolean authoritiesImpowerUser(AuthoritiesForUserReq req) {
        return null;
    }


    public UserAuthoritiesPageResp userAuthoritiesPageView(UserAuthoritiesPageReq req) {
        //在这里完成查询语句的封装
        Page<MetaverseUserDO> page = new Page<>(req.getCurrentPage(), req.getPageSize());

        LambdaQueryWrapper<MetaverseUserDO> queryWrapper = new LambdaQueryWrapper<>();
        if (req.getUserIds() != null) {
            queryWrapper.eq(MetaverseUserDO::getId, req.getUserIds());
        }
        if (req.getUsername() != null) {
            queryWrapper.likeRight(MetaverseUserDO::getUsername, req.getUsername());
        }
        if (req.getRegionId() != null) {
            queryWrapper.eq(MetaverseUserDO::getRegionId, req.getRegionId());
        }
        if (req.getEmail() != null) {
            queryWrapper.eq(MetaverseUserDO::getEmail, req.getEmail());
        }
        //查询包含该权限的用户
//        if (req.getPermissionId() != null) {
//            queryWrapper.eq(MetaverseUserDO::get, req.getUsername());
//        }
        if (req.getBirthTime() != null) {
            queryWrapper.eq(MetaverseUserDO::getBirthTime, req.getBirthTime());
        }
        if (req.getGender() != null) {
            queryWrapper.eq(MetaverseUserDO::getGender, req.getGender());
        }

        // 执行分页查询
        Page<MetaverseUserDO> userAuthorityPage = metaverseUserMapper.selectPage(page, queryWrapper);
        // 将查询结果转换为UserAuthoritiesPageResp对象

        return convertPageToResponse(userAuthorityPage);
    }

    private UserAuthoritiesPageResp convertPageToResponse(Page<MetaverseUserDO> userAuthorityPage) {
        if (Objects.isNull(userAuthorityPage)) {
            return null;
        }
        //todo 缺少一个权限转换
        return null;
    }


}
