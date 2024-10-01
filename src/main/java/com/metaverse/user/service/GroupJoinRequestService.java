package com.metaverse.user.service;

import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.user.db.entity.MetaverseGroupJoinRequestDO;
import com.metaverse.user.db.service.IMetaverseGroupJoinRequestService;
import com.metaverse.user.dto.MetaverseUserAbstractInfo;
import com.metaverse.user.req.AddGroupReq;
import com.metaverse.user.req.GroupReq;
import com.metaverse.user.resp.MetaverseGroupRequestResp;
import com.metaverse.user.resp.UserGroupQuestionResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupJoinRequestService {
    private final IMetaverseGroupJoinRequestService groupJoinRequestService;
    private final UserGroupMemberService groupMemberService;
    private final GroupQuestionService groupQuestionService;
    private final UserService userService;

    private static class UserGroupRequestStatus {
        public static final int PENDING = 0;
        public static final int AGREE = 1;
        public static final int REJECT = 2;
    }

    @Transactional(rollbackFor = Exception.class)
    public UserGroupQuestionResp joinGroupRequest(Long currentUserId, AddGroupReq req) {
        String message = req.getMessage();
        Long receiverGroupId = req.getReceiverGroupId();
        boolean existsed = groupJoinRequestService.lambdaQuery()
                .eq(MetaverseGroupJoinRequestDO::getGroupId, receiverGroupId)
                .eq(MetaverseGroupJoinRequestDO::getRequesterId, currentUserId)
                .eq(MetaverseGroupJoinRequestDO::getStatus, UserGroupRequestStatus.PENDING)
                .last(RepositoryConstant.FOR_SHARE)
                .exists();
        if (existsed) {
            return null;
        }
        groupJoinRequestService.save(new MetaverseGroupJoinRequestDO()
                .setGroupId(receiverGroupId)
                .setRequesterId(currentUserId)
                .setStatus(UserGroupRequestStatus.PENDING)
                .setVersion(0L)
                .setRequestMessage(message)
                .setRequestTime(LocalDateTime.now()));
        return groupQuestionService.findGroupQuestion(receiverGroupId);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean agreeGroupRequest(Long currentUserId, GroupReq req) {
        Long senderId = req.getSenderId();
        Long groupId = req.getGroupId();
        MetaverseGroupJoinRequestDO requestDO = assertNotExistWriteLoadGroupJoinRequest(groupId, senderId);
        groupJoinRequestService.lambdaUpdate()
                .eq(MetaverseGroupJoinRequestDO::getGroupId, groupId)
                .eq(MetaverseGroupJoinRequestDO::getRequesterId, senderId)
                .eq(MetaverseGroupJoinRequestDO::getStatus, UserGroupRequestStatus.PENDING)
                .set(MetaverseGroupJoinRequestDO::getStatus, UserGroupRequestStatus.AGREE)
                .set(MetaverseGroupJoinRequestDO::getUpdateBy, currentUserId)
                .set(MetaverseGroupJoinRequestDO::getVersion, requestDO.getVersion() + 1)
                .update();
        groupMemberService.agreeUserJoinGroup(currentUserId, senderId, groupId);
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean rejectGroupRequest(Long currentUserId, GroupReq req) {
        Long senderId = req.getSenderId();
        Long groupId = req.getGroupId();
        MetaverseGroupJoinRequestDO requestDO = assertNotExistWriteLoadGroupJoinRequest(groupId, senderId);
        groupJoinRequestService.lambdaUpdate()
                .eq(MetaverseGroupJoinRequestDO::getGroupId, groupId)
                .eq(MetaverseGroupJoinRequestDO::getRequesterId, senderId)
                .eq(MetaverseGroupJoinRequestDO::getStatus, UserGroupRequestStatus.PENDING)
                .set(MetaverseGroupJoinRequestDO::getStatus, UserGroupRequestStatus.REJECT)
                .set(MetaverseGroupJoinRequestDO::getUpdateBy, currentUserId)
                .set(MetaverseGroupJoinRequestDO::getVersion, requestDO.getVersion() + 1)
                .update();
        return true;
    }

    @NotNull
    private MetaverseGroupJoinRequestDO assertNotExistWriteLoadGroupJoinRequest(Long groupId, Long senderId) {
        MetaverseGroupJoinRequestDO requestDO = groupJoinRequestService.lambdaQuery()
                .eq(MetaverseGroupJoinRequestDO::getGroupId, groupId)
                .eq(MetaverseGroupJoinRequestDO::getRequesterId, senderId)
                .eq(MetaverseGroupJoinRequestDO::getStatus, UserGroupRequestStatus.PENDING)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        if (requestDO == null) {
            throw new IllegalArgumentException("未找到该用户入群请求");
        }
        return requestDO;
    }

    @Transactional(rollbackFor = Exception.class)
    public List<MetaverseGroupRequestResp> getUnagreedGroupRequestsOnTargetGroup(Long groupId) {
        return groupJoinRequestService.lambdaQuery()
                .eq(MetaverseGroupJoinRequestDO::getGroupId, groupId)
                .eq(MetaverseGroupJoinRequestDO::getStatus, UserGroupRequestStatus.PENDING)
                .list()
                .stream()
                .map(this::convertGroupRequestResp)
                .collect(Collectors.toList());
    }

    @Transactional(rollbackFor = Exception.class)
    public List<MetaverseGroupRequestResp> getGroupRequestsOnTargetGroup(Long groupId) {
        return groupJoinRequestService.lambdaQuery()
                .eq(MetaverseGroupJoinRequestDO::getGroupId, groupId)
                .list()
                .stream()
                .map(this::convertGroupRequestResp)
                .collect(Collectors.toList());
    }

    private MetaverseGroupRequestResp convertGroupRequestResp(MetaverseGroupJoinRequestDO groupJoinRequestDO) {
        if (groupJoinRequestDO == null) {
            return null;
        }
        Long requesterId = groupJoinRequestDO.getRequesterId();
        MetaverseUserAbstractInfo userAbstractInfo = userService.findUserInfoByUserId(requesterId);

        return new MetaverseGroupRequestResp()
                .setUserId(requesterId)
                .setName(userAbstractInfo.getName())
                .setBirthTime(userAbstractInfo.getBirthTime())
                .setAvatarFileId(userAbstractInfo.getAvatarImageId())
                .setGender(userAbstractInfo.getGender())
                .setRequestMessage(groupJoinRequestDO.getRequestMessage())
                .setStatus(groupJoinRequestDO.getStatus())
                .setRequestTime(groupJoinRequestDO.getRequestTime());
    }
}
