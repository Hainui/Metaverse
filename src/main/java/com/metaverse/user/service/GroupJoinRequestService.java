package com.metaverse.user.service;

import cn.hutool.core.util.StrUtil;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.user.db.entity.MetaverseGroupJoinRequestDO;
import com.metaverse.user.db.entity.MetaverseGroupQuestionDO;
import com.metaverse.user.db.service.IMetaverseGroupJoinRequestService;
import com.metaverse.user.db.service.IMetaverseGroupQuestionService;
import com.metaverse.user.req.AddGroupReq;
import com.metaverse.user.req.AgreeGroupReq;
import com.metaverse.user.req.AnswerGroupQuestionReq;
import com.metaverse.user.resp.UserGroupQuestionResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupJoinRequestService {

    private final IMetaverseGroupJoinRequestService groupJoinRequestService;
    private final IMetaverseGroupQuestionService groupQuestionService;
    private final UserGroupMemberService groupMemberService;

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
        return convertToQuestionResp(groupQuestionService.lambdaQuery().eq(MetaverseGroupQuestionDO::getGroupId, receiverGroupId).eq(MetaverseGroupQuestionDO::getEnabled, Boolean.TRUE).one());
    }

    private UserGroupQuestionResp convertToQuestionResp(MetaverseGroupQuestionDO questionDO) {
        if (questionDO == null) {
            return null;
        }
        return new UserGroupQuestionResp()
                .setGroupId(questionDO.getGroupId())
                .setQuestion(questionDO.getQuestion());
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean agreeGroupRequest(Long currentUserId, AgreeGroupReq req) {
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
    public Boolean answerGroupQuestion(AnswerGroupQuestionReq req, Long currentUserId) {
        Long groupId = req.getGroupId();
        MetaverseGroupQuestionDO groupQuestionDO = groupQuestionService.lambdaQuery()
                .eq(MetaverseGroupQuestionDO::getGroupId, groupId)
                .one();
        if (groupQuestionDO.getEnabled() && StrUtil.equals(req.getQuestionAnswer(), groupQuestionDO.getAnswer())) {
            return agreeGroupRequest(Objects.isNull(groupQuestionDO.getUpdateBy()) ? groupQuestionDO.getCreateBy() : groupQuestionDO.getUpdateBy(), new AgreeGroupReq(groupId, currentUserId));
        }
        return false;
    }
}
