package com.metaverse.user.service;

import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.user.db.entity.MetaverseGroupOperationLogDO;
import com.metaverse.user.db.entity.MetaverseUserGroupMemberDO;
import com.metaverse.user.db.service.IMetaverseGroupOperationLogService;
import com.metaverse.user.db.service.IMetaverseUserGroupMemberService;
import com.metaverse.user.req.GrantAdministratorReq;
import com.metaverse.user.req.InviteUserJoinGroupReq;
import com.metaverse.user.resp.UserGroupMemberResp;
import com.metaverse.user.resp.UserGroupResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGroupMemberService {

    private final IMetaverseUserGroupMemberService userGroupMemberService;
    private final IMetaverseGroupOperationLogService groupOperationLogService;


    private static class UserGroupMemberRole {
        public static final int GROUP_OWNER = 2;
        public static final int GROUP_MANAGER = 1;
        public static final int ORDINARY_MEMBER = 0;
    }

    private static class UserGroupOperationLog {
        public static final int PASSIVE_EXIT = 1;
        public static final int PASSIVE_ENTRY = 2;
        public static final int ACTIVE_ENTRY = 3;
        public static final int ACTIVE_EXIT = 4;
    }

    public Boolean memberUserIsManagement(Long currentUserId, Long groupId) {
        return userGroupMemberService.lambdaQuery()
                .eq(MetaverseUserGroupMemberDO::getGroupId, groupId)
                .eq(MetaverseUserGroupMemberDO::getMemberId, currentUserId)
                .in(MetaverseUserGroupMemberDO::getRole, Arrays.asList(UserGroupMemberRole.GROUP_MANAGER, UserGroupMemberRole.GROUP_OWNER))
                .exists();
    }

    public Boolean currentUserIsTargetGroupOwner(Long currentUserId, Long groupId) {
        return userGroupMemberService.lambdaQuery()
                .eq(MetaverseUserGroupMemberDO::getGroupId, groupId)
                .eq(MetaverseUserGroupMemberDO::getMemberId, currentUserId)
                .eq(MetaverseUserGroupMemberDO::getRole, UserGroupMemberRole.GROUP_OWNER)
                .exists();
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean inviteUserJoinGroup(Long currentUserId, InviteUserJoinGroupReq req) {
        Long userId = req.getUserId();
        Long groupId = req.getGroupId();
        LocalDateTime now = LocalDateTime.now();
        userGroupMemberService.save(new MetaverseUserGroupMemberDO()
                .setJoinedAt(now)
                .setRole(UserGroupMemberRole.ORDINARY_MEMBER)
                .setMemberId(userId)
                .setGroupId(groupId)
                .setVersion(0L));
        saveGroupOperationLog(currentUserId, groupId, userId, now, UserGroupOperationLog.PASSIVE_ENTRY);
        return true;
    }

    /**
     * 同意用户主动入群 访问级别为包级别 因为这个方法是 提供给群组请求的service用的
     *
     * @param currentUserId 同意用户入群的管理层用户id
     * @param senderId      主动请求的用户id
     * @param groupId       群组id
     */
    void agreeUserJoinGroup(Long currentUserId, Long senderId, Long groupId) {
        LocalDateTime now = LocalDateTime.now();
        userGroupMemberService.save(new MetaverseUserGroupMemberDO()
                .setJoinedAt(now)
                .setRole(UserGroupMemberRole.ORDINARY_MEMBER)
                .setMemberId(senderId)
                .setGroupId(groupId)
                .setVersion(0L));
        saveGroupOperationLog(currentUserId, groupId, senderId, now, UserGroupOperationLog.ACTIVE_ENTRY);
    }

    void saveGroupOperationLog(Long currentUserId, Long groupId, Long senderId, LocalDateTime now, Integer operationType) {
        groupOperationLogService.save(new MetaverseGroupOperationLogDO()
                .setGroupId(groupId)
                .setTargetId(senderId)
                .setOperatorId(currentUserId)
                .setOperationTime(now)
                .setOperationType(operationType));
    }

    public UserGroupResp getTargetGroupAllUsers(Long groupId) {
        List<MetaverseUserGroupMemberDO> userGroupMembers = userGroupMemberService.lambdaQuery()
                .eq(MetaverseUserGroupMemberDO::getGroupId, groupId)
                .list();
        return new UserGroupResp().setMembers(convertMembersToResp(userGroupMembers));
    }

    private List<UserGroupMemberResp> convertMembersToResp(List<MetaverseUserGroupMemberDO> userGroupMembers) {
        List<UserGroupMemberResp> members = new ArrayList<>();
        for (MetaverseUserGroupMemberDO member : userGroupMembers) {
            UserGroupMemberResp memberResp = new UserGroupMemberResp()
                    .setMemberId(member.getMemberId())
                    .setRole(member.getRole())
                    .setJoinedAt(member.getJoinedAt());
            members.add(memberResp);
        }
        return members;
    }


    @Transactional(rollbackFor = Exception.class)
    public boolean grantAdministrator(Long currentUserId, GrantAdministratorReq req) {
        Long groupId = req.getGroupId();
        Long memberId = req.getMemberId();
        MetaverseUserGroupMemberDO memberDO = userGroupMemberService.lambdaQuery()
                .eq(MetaverseUserGroupMemberDO::getGroupId, groupId)
                .eq(MetaverseUserGroupMemberDO::getMemberId, memberId)
                .eq(MetaverseUserGroupMemberDO::getRole, UserGroupMemberRole.ORDINARY_MEMBER)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        boolean noChange = Objects.isNull(memberDO);
        if (noChange) {
            return false;
        }
        userGroupMemberService.lambdaUpdate()
                .eq(MetaverseUserGroupMemberDO::getGroupId, groupId)
                .eq(MetaverseUserGroupMemberDO::getMemberId, memberId)
                .eq(MetaverseUserGroupMemberDO::getRole, UserGroupMemberRole.ORDINARY_MEMBER)
                .set(MetaverseUserGroupMemberDO::getRole, UserGroupMemberRole.GROUP_MANAGER)
                .set(MetaverseUserGroupMemberDO::getUpdateBy, currentUserId)
                .set(MetaverseUserGroupMemberDO::getVersion, memberDO.getVersion() + 1)
                .update();
        return true;
    }
}
