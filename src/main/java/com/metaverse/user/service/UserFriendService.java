package com.metaverse.user.service;

import cn.hutool.core.util.StrUtil;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.user.db.entity.*;
import com.metaverse.user.db.service.*;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.req.AddFriendReq;
import com.metaverse.user.req.AnswerUserQuestionReq;
import com.metaverse.user.resp.MetaverseFriendRequestResp;
import com.metaverse.user.resp.UserFriendQuestionResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserFriendService {

    private final IMetaverseFriendRequestService friendRequestService;
    private final IMetaverseUserFriendService userFriendService;
    private final IMetaverseUserService userService;
    private final IMetaverseUserFriendQuestionService userFriendQuestionService;
    private final IMetaverseUserFriendOperationLogService userFriendOperationLogService;

    @Transactional(rollbackFor = Exception.class)
    public UserFriendQuestionResp addFriend(AddFriendReq req, Long senderId) {
        Long receiverId = req.getReceiverId();
        String message = req.getMessage();
        Set<Long> blacklistId = userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, receiverId)
                .eq(MetaverseUserFriendDO::getRelation, 2)
                .list()
                .stream()
                .map(MetaverseUserFriendDO::getFriendId)
                .collect(Collectors.toSet());
        if (blacklistId.contains(senderId)) {
            return null;
        }
        boolean existsed = friendRequestService.lambdaQuery()
                .eq(MetaverseFriendRequestDO::getSenderId, senderId)
                .eq(MetaverseFriendRequestDO::getReceiverId, receiverId)
                .ne(MetaverseFriendRequestDO::getStatus, 2)
                .last(RepositoryConstant.FOR_SHARE)
                .exists();
        if (existsed) {
            return null;
        }
        friendRequestService.save(new MetaverseFriendRequestDO()
                .setSenderId(senderId)
                .setReceiverId(receiverId)
                .setMessage(message)
                .setCreatedAt(LocalDateTime.now())
                .setStatus(0)
                .setVersion(0L));
        return convertToQuestionResp(userFriendQuestionService.getById(receiverId));
    }

    private UserFriendQuestionResp convertToQuestionResp(MetaverseUserFriendQuestionDO questionDO) {
        if (questionDO == null || questionDO.getQuestion() == null || !questionDO.getEnabled()) {
            return null;
        }
        return new UserFriendQuestionResp().setQuestion(questionDO.getQuestion()).setUserId(questionDO.getUserId());
    }

    public List<MetaverseFriendRequestResp> getFriendRequestsOnYourself(Long currentUserId, Integer status) {
        return friendRequestService.lambdaQuery()
                .eq(MetaverseFriendRequestDO::getReceiverId, currentUserId)
                .eq(status != null, MetaverseFriendRequestDO::getStatus, status)
                .list()
                .stream()
                .map(this::convertFriendRequestResp)
                .collect(Collectors.toList());
    }

    private MetaverseFriendRequestResp convertFriendRequestResp(MetaverseFriendRequestDO metaverseFriendRequestDO) {
        if (metaverseFriendRequestDO == null) {
            return null;
        }
        Long senderId = metaverseFriendRequestDO.getSenderId();
        MetaverseUserDO userDO = userService.getById(senderId);

        return new MetaverseFriendRequestResp()
                .setUserId(senderId)
                .setName(userDO.getUsername())
                .setGender(MetaverseUser.Gender.convertGender(userDO.getGender()))
                .setBirthTime(userDO.getBirthTime())
                .setMessage(metaverseFriendRequestDO.getMessage())
                .setAvatarFileId(userDO.getAvatarFileId())
                .setStatus(metaverseFriendRequestDO.getStatus())
                .setCreatedAt(metaverseFriendRequestDO.getCreatedAt());
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean agreeFriendRequest(Long currentUserId, Long senderId) {
        MetaverseFriendRequestDO one = friendRequestService.lambdaQuery()
                .eq(MetaverseFriendRequestDO::getReceiverId, currentUserId)
                .eq(MetaverseFriendRequestDO::getSenderId, senderId)
                .eq(MetaverseFriendRequestDO::getStatus, 0)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();

        friendRequestService.lambdaUpdate()
                .eq(MetaverseFriendRequestDO::getReceiverId, currentUserId)
                .eq(MetaverseFriendRequestDO::getSenderId, senderId)
                .eq(MetaverseFriendRequestDO::getStatus, 0)
                .set(MetaverseFriendRequestDO::getStatus, 1)
                .set(MetaverseFriendRequestDO::getUpdateBy, currentUserId)
                .set(MetaverseFriendRequestDO::getVersion, one.getVersion() + 1);

        LocalDateTime now = LocalDateTime.now();
        userFriendService.save(new MetaverseUserFriendDO()
                .setUserId(currentUserId)
                .setFriendId(senderId)
                .setVersion(0L)
                .setCreatedAt(now)
                .setIntimacyLevel(new BigDecimal(0))
                .setStatus(1)
                .setRelation(1));

        userFriendOperationLogService.save(new MetaverseUserFriendOperationLogDO()
                .setUserId(currentUserId)
                .setVersion(0L)
                .setOperationTime(now)
                .setTargetId(senderId)
                .setOperationType(1));
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean answerUserQuestion(AnswerUserQuestionReq req, Long currentUserId) {
        Long receiverId = req.getReceiverId();
        MetaverseUserFriendQuestionDO userQuestion = userFriendQuestionService.getById(receiverId);
        if (userQuestion.getEnabled() && StrUtil.equals(req.getQuestionAnswer(), userQuestion.getCorrectAnswer())) {
            return agreeFriendRequest(receiverId, currentUserId);
        }
        return false;
    }
}
