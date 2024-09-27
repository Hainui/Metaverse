package com.metaverse.user.service;

import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.user.db.entity.MetaverseGroupJoinRequestDO;
import com.metaverse.user.db.entity.MetaverseGroupQuestionDO;
import com.metaverse.user.db.service.IMetaverseGroupJoinRequestService;
import com.metaverse.user.db.service.IMetaverseGroupQuestionService;
import com.metaverse.user.req.AddGroupReq;
import com.metaverse.user.resp.UserGroupQuestionResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupJoinRequestService {

    private final IMetaverseGroupJoinRequestService groupJoinRequestService;
    private final IMetaverseGroupQuestionService groupQuestionService;

    private static class UserGroupRequestStatus {
        public static final int PENDING = 0;
        public static final int AGREE = 1;
        public static final int REJECT = 2;
    }

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
        return convertToQuestionResp(groupQuestionService.lambdaQuery().eq(MetaverseGroupQuestionDO::getGroupId, receiverGroupId).one());
    }

    private UserGroupQuestionResp convertToQuestionResp(MetaverseGroupQuestionDO questionDO) {
        if (questionDO == null) {
            return null;
        }
        return new UserGroupQuestionResp()
                .setGroupId(questionDO.getGroupId())
                .setQuestion(questionDO.getQuestion());
    }
}
