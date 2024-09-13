package com.metaverse.permission.service;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.metaverse.common.Utils.PermissionComparator;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.permission.MetaverseUserPermissionRelationshipIdGen;
import com.metaverse.permission.db.entity.MetaversePermissionDO;
import com.metaverse.permission.db.entity.MetaverseUserPermissionRelationshipDO;
import com.metaverse.permission.db.entity.MetaverseUserPermissionRelationshipDeleteDO;
import com.metaverse.permission.db.service.IMetaversePermissionService;
import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipDeleteService;
import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipService;
import com.metaverse.permission.domain.MetaversePermission;
import com.metaverse.permission.req.AuthoritiesForUserReq;
import com.metaverse.permission.req.AuthoritiesForUsersReq;
import com.metaverse.permission.req.UserAuthoritiesPageReq;
import com.metaverse.permission.resp.MetaversePermissionResp;
import com.metaverse.permission.resp.UserAuthoritiesPageResp;
import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.db.service.IMetaverseRegionService;
import com.metaverse.region.resp.MetaverseRegionResp;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.db.mapper.MetaverseUserMapper;
import com.metaverse.user.db.service.IMetaverseUserService;
import com.metaverse.user.domain.MetaverseUser;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaverseUserPermissionRelationshipService {
    private final IMetaverseUserService metaverseUserService;
    private final IMetaverseUserPermissionRelationshipService permissionRelationshipService;
    private final IMetaverseRegionService regionService;
    private final IMetaversePermissionService metaversePermissionService;
    private final MetaverseUserPermissionRelationshipIdGen metaverseUserPermissionRelationshipIdGen;
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


    public List<UserAuthoritiesPageResp> userAuthoritiesPageView(UserAuthoritiesPageReq req) {
        QueryWrapper<MetaverseUserDO> queryWrapper = new QueryWrapper<>();
        Long userId = req.getUserId();
        Long permissionId = req.getPermissionId();
        if (permissionId != null) {
            Set<Long> userIds = permissionRelationshipService.lambdaQuery()
                    .select(MetaverseUserPermissionRelationshipDO::getUserId)
                    .eq(MetaverseUserPermissionRelationshipDO::getPermissionId, permissionId)
                    .list()
                    .stream()
                    .map(MetaverseUserPermissionRelationshipDO::getUserId)
                    .collect(Collectors.toSet());
            if (userId != null) {
                if (userIds.contains(userId)) {
                    queryWrapper.eq("id", userId);
                } else {
                    return Collections.emptyList();
                }
            } else {
                queryWrapper.in("id", userIds);
            }
        } else {
            queryWrapper.eq(userId != null, "id", userId);
        }
        // 构建查询条件
        queryWrapper.likeRight(StringUtils.isNotBlank(req.getUsername()), "username", req.getUsername());
        queryWrapper.eq(req.getRegionId() != null, "region_id", req.getRegionId());
        queryWrapper.eq(req.getEmail() != null, "email", req.getEmail());
        queryWrapper.ge(req.getBirthTime() != null, "birth_time", req.getBirthTime());
        queryWrapper.eq(req.getGender() != null, "gender", req.getGender());
        Page<MetaverseUserDO> metaverseUserDOPage = metaverseUserService.getBaseMapper().selectPage(new Page<>(req.getCurrentPage(), req.getPageSize()), queryWrapper);
        return metaverseUserDOPage.getRecords().stream().map(this::convertToPageResp).collect(Collectors.toList());
    }

    private UserAuthoritiesPageResp convertToPageResp(MetaverseUserDO metaverseUserDO) {
        Long userId = metaverseUserDO.getId();
        List<MetaversePermissionDO> permissionDOList = metaversePermissionService.listByIds(permissionRelationshipService.lambdaQuery()
                .select(MetaverseUserPermissionRelationshipDO::getPermissionId)
                .eq(MetaverseUserPermissionRelationshipDO::getUserId, userId)
                .list()
                .stream()
                .map(MetaverseUserPermissionRelationshipDO::getPermissionId)
                .collect(Collectors.toList()));

        List<String> permission = permissionDOList.stream().flatMap(permissionDO -> JSONArray.parseArray(permissionDO.getPermissions(), String.class).stream()).collect(Collectors.toList());

        return new UserAuthoritiesPageResp()
                .setUserId(userId)
                .setUsername(metaverseUserDO.getUsername())
                .setEmail(metaverseUserDO.getEmail())
                .setBirthTime(metaverseUserDO.getBirthTime())
                .setGender(metaverseUserDO.getGender())
                .setRegionResp(convertToRegionResp(regionService.getById(metaverseUserDO.getRegionId())))
                .setPermissionRespList(permissionDOList.stream().map(this::convertPermissionResp).collect(Collectors.toList()))
                .setAuthorizationLevel(calculateAuthorizationLevel(permission));
    }

    /**
     * 计算该用户所有权限串能开门的百分比
     * 计算公式：(用户所有的权限串数量/系统所有权限串数量)*100% 结果向下取整保留两位小数
     * 读取配置获取所有权限串
     *
     * @param permissions 用户具备的权限串
     * @return 结果向下取整保留两位小数 如：78.34%
     */
    private String calculateAuthorizationLevel(List<String> permissions) {
        return "55.65%";
    }

    private MetaversePermissionResp convertPermissionResp(MetaversePermissionDO metaversePermissionDO) {
        if (Objects.isNull(metaversePermissionDO)) {
            return null;
        }
        return new MetaversePermissionResp()
                .setId(metaversePermissionDO.getId())
                .setPermissionGroupName(metaversePermissionDO.getPermissionGroupName());
    }

    private MetaverseRegionResp convertToRegionResp(MetaverseRegionDO regionDO) {
        if (Objects.isNull(regionDO)) {
            return null;
        }
        return new MetaverseRegionResp()
                .setId(regionDO.getId())
                .setName(regionDO.getName());
    }

}
