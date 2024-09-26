package com.metaverse.permission.service;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.metaverse.common.Utils.PermissionComparator;
import com.metaverse.common.config.PermissionProperties;
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
import com.metaverse.user.db.service.IMetaverseUserService;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
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
    private final PermissionProperties permissionProperties;
    private final UserService userService;

    @Transactional(rollbackFor = Exception.class)
    public Boolean authoritiesImpowerUsers(AuthoritiesForUsersReq req, Long currentUserId) {
        List<Long> userIds = req.getUserIds();
        List<MetaverseUser> metaverseUsers = MetaverseUser.readLoadAndAssertNotExist(userIds);
        List<MetaversePermission> newMetaversePermissions = PermissionComparator.filterIncludedPermissions(MetaversePermission.readLoadAndAssertNotExist(req.getPermissionIds()));
        for (MetaverseUser metaverseUser : metaverseUsers) {
            List<String> userOldPermissions = metaverseUser.getPermissions().stream().flatMap(Permission -> Permission.getPermissions().stream()).collect(Collectors.toList());
            for (MetaversePermission newMetaversePermission : newMetaversePermissions) {
                int code = PermissionComparator.compareLists(userOldPermissions, newMetaversePermission.getPermissions());
                if (code != 1) {
                    userService.signOut(metaverseUser.getId());
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

        // 挨个为每个用户添加权限
        userIds.forEach(userId ->
                newMetaversePermissions.forEach(newMetaversePermission -> permissionRelationshipService.save(new MetaverseUserPermissionRelationshipDO()
                        .setId(metaverseUserPermissionRelationshipIdGen.nextId())
                        .setUserId(userId)
                        .setPermissionId(newMetaversePermission.getId())
                        .setImpowerAt(LocalDateTime.now())
                        .setImpowerBy(currentUserId)))
        );

        return userService.signOut(userIds);
    }

    private MetaverseUserPermissionRelationshipDeleteDO convertToRelationshipDeleteDo(MetaverseUserPermissionRelationshipDO permissionRelationshipDO, Long currentUserId, LocalDateTime now) {
        if (Objects.isNull(permissionRelationshipDO)) {
            return null;
        }
        return new MetaverseUserPermissionRelationshipDeleteDO()
                .setId(permissionRelationshipDO.getId())
                .setUserId(permissionRelationshipDO.getUserId())
                .setPermissionId(permissionRelationshipDO.getPermissionId())
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
                    userService.signOut(userId);
                }
            }
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean authoritiesRevokeForUser(AuthoritiesForUserReq req, Long currentUserId) {
        Long userId = req.getUserId();
        List<Long> deletePermissionIds = req.getPermissionIds();
        List<MetaverseUserPermissionRelationshipDO> relationshipDOs = permissionRelationshipService.lambdaQuery()
                .eq(MetaverseUserPermissionRelationshipDO::getUserId, userId)
                .last(RepositoryConstant.FOR_UPDATE)
                .list();
        boolean isChanged = false;
        for (MetaverseUserPermissionRelationshipDO relationshipDO : relationshipDOs) {
            Long oldPermissionId = relationshipDO.getPermissionId();
            if (deletePermissionIds.contains(oldPermissionId)) {
                permissionRelationshipService.remove(new LambdaQueryWrapper<MetaverseUserPermissionRelationshipDO>()
                        .eq(MetaverseUserPermissionRelationshipDO::getUserId, userId)
                        .eq(MetaverseUserPermissionRelationshipDO::getPermissionId, oldPermissionId));
                permissionRelationshipDeleteService.save(convertToRelationshipDeleteDo(relationshipDO, currentUserId, LocalDateTime.now()));
                isChanged = true;
            }
        }
        if (isChanged) {
            return userService.signOut(userId);
        }
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean authoritiesImpowerUser(AuthoritiesForUserReq req, Long currentUserId) {
        Long userId = req.getUserId();
        MetaverseUser metaverseUser = MetaverseUser.readLoadAndAssertNotExist(userId);
        List<MetaversePermission> newMetaversePermissions = PermissionComparator.filterIncludedPermissions(MetaversePermission.readLoadAndAssertNotExist(req.getPermissionIds()));
        List<String> userOldPermissions = metaverseUser.getPermissions().stream().flatMap(Permission -> Permission.getPermissions().stream()).collect(Collectors.toList());
        boolean isChanged = false;
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
                userService.signOut(userId);
                isChanged = true;
            }
        }
        if (isChanged) {
            return userService.signOut(userId);
        }
        return true;
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

        List<Long> permissionIds = permissionRelationshipService.lambdaQuery()
                .select(MetaverseUserPermissionRelationshipDO::getPermissionId)
                .eq(MetaverseUserPermissionRelationshipDO::getUserId, userId)
                .list()
                .stream()
                .map(MetaverseUserPermissionRelationshipDO::getPermissionId)
                .collect(Collectors.toList());
        List<MetaversePermissionDO> permissionDOList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(permissionIds)) {
            permissionDOList = metaversePermissionService.listByIds(permissionIds);
        }
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
        int unrestrictedAccessSize = PermissionProperties.UNRESTRICTED_ACCESS_SIZE;
        List<String> systemPermissions = permissionProperties.getSystemPermissions();
//        if (permissions == null || permissions.isEmpty()) {
//            return "0.00%";
//        }
        int successfulAccessCount = 0;
        for (String systemPermission : systemPermissions) {
            if (PermissionComparator.isPermissionMatched(systemPermission, permissions)) {
                successfulAccessCount++;
            }
        }
        BigDecimal result = BigDecimal.valueOf(successfulAccessCount + unrestrictedAccessSize)
                .divide(BigDecimal.valueOf(systemPermissions.size() + unrestrictedAccessSize), 4, RoundingMode.DOWN)
                .multiply(BigDecimal.valueOf(100));
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(result) + "%";
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
