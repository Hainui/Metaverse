package com.metaverse.permission.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipDeleteService;
import com.metaverse.permission.db.service.IMetaverseUserPermissionRelationshipService;
import com.metaverse.permission.domain.MetaversePermission;
import com.metaverse.permission.req.AuthoritiesForUserReq;
import com.metaverse.permission.req.AuthoritiesForUsersReq;
import com.metaverse.permission.req.UserAuthoritiesPageReq;
import com.metaverse.permission.resp.UserAuthoritiesPageResp;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.db.mapper.MetaverseUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetaverseUserPermissionRelationshipService {
    private final IMetaverseUserPermissionRelationshipService permissionRelationshipService;
    private final IMetaverseUserPermissionRelationshipDeleteService permissionRelationshipDeleteService;//备份 上面删除的信息下面要留作备份
    private final MetaverseUserMapper metaverseUserMapper;

    public Boolean authoritiesImpowerUsers(AuthoritiesForUsersReq req) {
        return null;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean authoritiesResetUsers(AuthoritiesForUsersReq req, Long currentUserId) {
        MetaversePermission permission = new MetaversePermission();
        //todo 遍历每个用户id进行校验是否存在
        for (Long userId : req.getUserIds()) {
            permission.writeLoadAndAssertNotExist(userId);
        }
        return permission.authoritiesResetUsers(req.getPermissionIds(), req.getUserIds(), currentUserId);


    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean authoritiesRevokeForUsers(AuthoritiesForUsersReq req, Long currentUserId) {
        MetaversePermission permission = new MetaversePermission();
        for (Long userId : req.getUserIds()) {
            permission.writeLoadAndAssertNotExist(userId);
        }
        return permission.authoritiesRevokeForUsers(req.getUserIds(), req.getPermissionIds(), currentUserId);
    }

    public Boolean authoritiesRevokeForUser(AuthoritiesForUserReq req, Long currentUserId) {
        MetaversePermission permission = new MetaversePermission();
        permission.writeLoadAndAssertNotExist(req.getUserId());
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
