package com.metaverse.user.service;

import com.metaverse.user.db.entity.MetaverseUserGroupMemberDO;
import com.metaverse.user.db.service.IMetaverseUserGroupMemberService;
import com.metaverse.user.domain.MetaverseUserGroup;
import com.metaverse.user.req.CreateUserGroupReq;
import com.metaverse.user.req.ModifyUserGroupReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGroupService {

    private final IMetaverseUserGroupMemberService userGroupMemberService;

    private interface UserGroupMemberRoleEnum {
        public static final int ORDINARY_MEMBER = 0;
        public static final int MANAGER = 1;
        public static final int GROUP_OWNER = 2;

    }

    public Long createUserGroup(Long currentUserId, CreateUserGroupReq req) {
        return MetaverseUserGroup.create(req.getGroupName(), req.getDescription(), currentUserId, Optional.of(req.getMemberIds()).orElse(Collections.emptyList()));
    }

    public boolean modifyUserGroup(Long currentUserId, ModifyUserGroupReq req) {
        MetaverseUserGroup metaverseUserGroup = MetaverseUserGroup.writeLoadAndAssertNotExist(req.getUserGroupId());
        return metaverseUserGroup.modifyGroupInfo(req.getGroupName(), req.getDescription(), currentUserId);
    }

    public Boolean memberUserIsManagement(Long currentUserId, Long groupId) {
        return userGroupMemberService.lambdaQuery()
                .eq(MetaverseUserGroupMemberDO::getGroupId, groupId)
                .eq(MetaverseUserGroupMemberDO::getMemberId, currentUserId)
                .in(MetaverseUserGroupMemberDO::getRole, Arrays.asList(UserGroupMemberRoleEnum.MANAGER, UserGroupMemberRoleEnum.GROUP_OWNER))
                .exists();
    }

    public Boolean currentUserIsTargetGroupOwner(Long currentUserId, Long groupId) {
        return userGroupMemberService.lambdaQuery()
                .eq(MetaverseUserGroupMemberDO::getGroupId, groupId)
                .eq(MetaverseUserGroupMemberDO::getMemberId, currentUserId)
                .eq(MetaverseUserGroupMemberDO::getRole, UserGroupMemberRoleEnum.GROUP_OWNER)
                .exists();
    }
}
