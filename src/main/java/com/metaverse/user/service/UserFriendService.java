package com.metaverse.user.service;

import cn.hutool.core.util.StrUtil;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.user.db.entity.MetaverseFriendRequestDO;
import com.metaverse.user.db.entity.MetaverseUserFriendDO;
import com.metaverse.user.db.entity.MetaverseUserFriendOperationLogDO;
import com.metaverse.user.db.entity.MetaverseUserFriendQuestionDO;
import com.metaverse.user.db.service.IMetaverseFriendRequestService;
import com.metaverse.user.db.service.IMetaverseUserFriendOperationLogService;
import com.metaverse.user.db.service.IMetaverseUserFriendQuestionService;
import com.metaverse.user.db.service.IMetaverseUserFriendService;
import com.metaverse.user.dto.MetaverseUserAbstractInfo;
import com.metaverse.user.req.AddFriendReq;
import com.metaverse.user.req.AnswerUserQuestionReq;
import com.metaverse.user.resp.MetaverseFriendRequestResp;
import com.metaverse.user.resp.UserFriendQuestionResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFriendService {
    private final IMetaverseFriendRequestService friendRequestService;
    private final IMetaverseUserFriendService userFriendService;
    private final UserService userService;
    private final IMetaverseUserFriendQuestionService userFriendQuestionService;
    private final IMetaverseUserFriendOperationLogService userFriendOperationLogService;

    private static class UserFriendRelation {
        public static final int FRIEND = 1;
        public static final int BLACKLIST = 2;
    }

    private static class UserFriendStatus {
        public static final int NORMAL = 1;
        public static final int DELETED = 2;
    }

    private static class UserFriendOperationLog {
        public static final int ADD_FRIEND = 1;
        public static final int DELETE_FRIEND = 2;
        public static final int BLOCK_FRIEND = 3;
        public static final int UNBLOCK_FRIEND = 4;
    }

    private static class UserFriendRequestStatus {
        public static final int PENDING = 0;
        public static final int AGREE = 1;
        public static final int REJECT = 2;
    }

    public boolean targetUserIsBlacklist(Long currentUserId, Long targetId) {
        return userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getFriendId, targetId)
                .eq(MetaverseUserFriendDO::getRelation, UserFriendRelation.BLACKLIST)
                .exists();
    }

    @Transactional(rollbackFor = Exception.class)
    public UserFriendQuestionResp addFriendRequest(AddFriendReq req, Long senderId) {
        Long receiverId = req.getReceiverId();
        String message = req.getMessage();
        Set<Long> blacklistId = userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, receiverId)
                .eq(MetaverseUserFriendDO::getRelation, UserFriendRelation.BLACKLIST)
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
                .eq(MetaverseFriendRequestDO::getStatus, UserFriendRequestStatus.PENDING)
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
                .setStatus(UserFriendRequestStatus.PENDING)
                .setVersion(0L));
        return convertToQuestionResp(userFriendQuestionService.lambdaQuery().eq(MetaverseUserFriendQuestionDO::getUserId, receiverId).eq(MetaverseUserFriendQuestionDO::getEnabled, Boolean.TRUE).one());
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
        MetaverseUserAbstractInfo userAbstractInfo = userService.findUserInfoByUserIds(Collections.singletonList(senderId)).get(0);

        return new MetaverseFriendRequestResp()
                .setUserId(senderId)
                .setName(userAbstractInfo.getName())
                .setBirthTime(userAbstractInfo.getBirthTime())
                .setAvatarFileId(userAbstractInfo.getAvatarImageId())
                .setGender(userAbstractInfo.getGender())
                .setMessage(metaverseFriendRequestDO.getMessage())
                .setStatus(metaverseFriendRequestDO.getStatus())
                .setCreatedAt(metaverseFriendRequestDO.getCreatedAt());
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean agreeFriendRequest(Long currentUserId, Long senderId) {
        MetaverseFriendRequestDO friendRequestDO = assertNotExistWriteLoadFriendRequest(currentUserId, senderId);
        friendRequestService.lambdaUpdate()
                .eq(MetaverseFriendRequestDO::getSenderId, senderId)
                .eq(MetaverseFriendRequestDO::getReceiverId, currentUserId)
                .eq(MetaverseFriendRequestDO::getStatus, UserFriendRequestStatus.PENDING)
                .set(MetaverseFriendRequestDO::getStatus, UserFriendRequestStatus.AGREE)
                .set(MetaverseFriendRequestDO::getUpdateBy, currentUserId)
                .set(MetaverseFriendRequestDO::getVersion, friendRequestDO.getVersion() + 1)
                .update();

        LocalDateTime now = LocalDateTime.now();

        List<Long> matchedCondition = Arrays.asList(currentUserId, senderId);

        MetaverseUserFriendDO userFriendDO = userFriendService.lambdaQuery()
                .in(MetaverseUserFriendDO::getUserId, matchedCondition)
                .in(MetaverseUserFriendDO::getFriendId, matchedCondition)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();

        if (userFriendDO == null) {
            userFriendService.save(new MetaverseUserFriendDO()
                    .setUserId(currentUserId)
                    .setFriendId(senderId)
                    .setVersion(0L)
                    .setCreatedAt(now)
                    .setIntimacyLevel(new BigDecimal(0))
                    .setStatus(UserFriendStatus.NORMAL)
                    .setRelation(UserFriendRelation.FRIEND));
            userFriendService.save(new MetaverseUserFriendDO()
                    .setUserId(senderId)
                    .setFriendId(currentUserId)
                    .setVersion(0L)
                    .setCreatedAt(now)
                    .setIntimacyLevel(new BigDecimal(0))
                    .setStatus(UserFriendStatus.NORMAL)
                    .setRelation(UserFriendRelation.FRIEND));
        } else {
            userFriendService.lambdaUpdate()
                    .in(MetaverseUserFriendDO::getUserId, matchedCondition)
                    .in(MetaverseUserFriendDO::getFriendId, matchedCondition)
                    .set(MetaverseUserFriendDO::getStatus, UserFriendStatus.NORMAL)
                    .set(MetaverseUserFriendDO::getRelation, UserFriendRelation.FRIEND)
                    .set(MetaverseUserFriendDO::getUpdateBy, currentUserId)
                    .set(MetaverseUserFriendDO::getVersion, userFriendDO.getVersion() + 1)
                    .set(MetaverseUserFriendDO::getIntimacyLevel, new BigDecimal(0))
                    .update();
        }
        saveUserFriendOperationLog(currentUserId, senderId, now, UserFriendOperationLog.ADD_FRIEND);
        return true;
    }


    @Transactional(rollbackFor = Exception.class)
    public Boolean rejectFriendRequest(Long currentUserId, Long senderId) {
        MetaverseFriendRequestDO friendRequestDO = assertNotExistWriteLoadFriendRequest(currentUserId, senderId);
        return friendRequestService.lambdaUpdate()
                .eq(MetaverseFriendRequestDO::getSenderId, currentUserId)
                .eq(MetaverseFriendRequestDO::getReceiverId, senderId)
                .eq(MetaverseFriendRequestDO::getStatus, UserFriendRequestStatus.PENDING)
                .set(MetaverseFriendRequestDO::getStatus, UserFriendRequestStatus.REJECT)
                .set(MetaverseFriendRequestDO::getVersion, friendRequestDO.getVersion() + 1)
                .update();
    }

    @NotNull
    private MetaverseFriendRequestDO assertNotExistWriteLoadFriendRequest(Long currentUserId, Long senderId) {
        MetaverseFriendRequestDO one = friendRequestService.lambdaQuery()
                .eq(MetaverseFriendRequestDO::getSenderId, senderId)
                .eq(MetaverseFriendRequestDO::getReceiverId, currentUserId)
                .eq(MetaverseFriendRequestDO::getStatus, UserFriendRequestStatus.PENDING)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        if (one == null) {
            throw new IllegalArgumentException("未找到该用户的好友请求记录");
        }
        return one;
    }

    private void saveUserFriendOperationLog(Long currentUserId, Long targetId, LocalDateTime now, Integer operationType) {
        userFriendOperationLogService.save(new MetaverseUserFriendOperationLogDO()
                .setUserId(currentUserId)
                .setVersion(0L)
                .setOperationTime(now)
                .setTargetId(targetId)
                .setOperationType(operationType));
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean answerUserQuestion(AnswerUserQuestionReq req, Long currentUserId) {
        Long receiverId = req.getReceiverId();
        MetaverseUserFriendQuestionDO userQuestion = userFriendQuestionService.lambdaQuery()
                .eq(MetaverseUserFriendQuestionDO::getUserId, receiverId)
                .one();
        if (userQuestion.getEnabled() && StrUtil.equals(req.getQuestionAnswer(), userQuestion.getCorrectAnswer())) {
            return agreeFriendRequest(Objects.isNull(userQuestion.getUpdateBy()) ? userQuestion.getCreateBy() : userQuestion.getUpdateBy(), currentUserId);
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean delFriend(Long targetId, Long currentUserId) {
        MetaverseUserFriendDO userFriendDO = assertNotExistAndWriteLoadUserFriend(currentUserId, targetId);
        boolean noChange = userFriendDO.getStatus().equals(UserFriendStatus.DELETED);
        if (noChange) {
            return false;
        }
        userFriendService.lambdaUpdate()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getFriendId, targetId)
                .set(MetaverseUserFriendDO::getStatus, UserFriendStatus.DELETED)
                .set(MetaverseUserFriendDO::getUpdateBy, currentUserId)
                .set(MetaverseUserFriendDO::getVersion, userFriendDO.getVersion() + 1)
                .set(MetaverseUserFriendDO::getIntimacyLevel, new BigDecimal(0))
                .update();
        saveUserFriendOperationLog(currentUserId, targetId, LocalDateTime.now(), UserFriendOperationLog.DELETE_FRIEND);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean blockFriend(Long targetId, Long currentUserId) {
        MetaverseUserFriendDO userFriendDO = assertNotExistAndWriteLoadUserFriend(currentUserId, targetId);
        boolean noChange = userFriendDO.getRelation().equals(UserFriendRelation.BLACKLIST);
        if (noChange) {
            return false;
        }
        userFriendService.lambdaUpdate()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getFriendId, targetId)
                .set(MetaverseUserFriendDO::getRelation, UserFriendRelation.BLACKLIST)
                .set(MetaverseUserFriendDO::getUpdateBy, currentUserId)
                .set(MetaverseUserFriendDO::getVersion, userFriendDO.getVersion() + 1)
                .set(MetaverseUserFriendDO::getIntimacyLevel, new BigDecimal(0))
                .update();
        saveUserFriendOperationLog(currentUserId, targetId, LocalDateTime.now(), UserFriendOperationLog.BLOCK_FRIEND);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean unblockFriends(Long targetId, Long currentUserId) {
        MetaverseUserFriendDO userFriendDO = assertNotExistAndWriteLoadUserFriend(currentUserId, targetId);
        boolean noChange = userFriendDO.getRelation().equals(UserFriendRelation.FRIEND);
        if (noChange) {
            return false;
        }
        userFriendService.lambdaUpdate()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getFriendId, targetId)
                .set(MetaverseUserFriendDO::getRelation, UserFriendRelation.FRIEND)
                .set(MetaverseUserFriendDO::getUpdateBy, currentUserId)
                .set(MetaverseUserFriendDO::getVersion, userFriendDO.getVersion() + 1)
                .set(MetaverseUserFriendDO::getIntimacyLevel, new BigDecimal(0))
                .update();
        saveUserFriendOperationLog(currentUserId, targetId, LocalDateTime.now(), UserFriendOperationLog.UNBLOCK_FRIEND);
        return true;
    }

    @NotNull
    public MetaverseUserFriendDO assertNotExistAndWriteLoadUserFriend(Long currentUserId, Long targetId) {
        MetaverseUserFriendDO userFriendDO = userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getFriendId, targetId)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        if (userFriendDO == null) {
            throw new IllegalArgumentException("未找到该好友");
        }
        return userFriendDO;
    }

    public Boolean targetUserIsFriend(Long currentUserId, Long targetId) {
        return userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getFriendId, targetId)
                .eq(MetaverseUserFriendDO::getStatus, UserFriendStatus.NORMAL)
                .eq(MetaverseUserFriendDO::getRelation, UserFriendRelation.FRIEND)
                .exists();
    }

    public List<MetaverseUserAbstractInfo> getAllFriends(Long currentUserId) {
        List<Long> friendIds = userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getStatus, UserFriendStatus.NORMAL)
                .eq(MetaverseUserFriendDO::getRelation, UserFriendRelation.FRIEND)
                .list()
                .stream()
                .map(MetaverseUserFriendDO::getFriendId)
                .collect(Collectors.toList());
        return userService.findUserInfoByUserIds(friendIds);
    }

    public List<MetaverseUserAbstractInfo> getAllBlackUsers(Long currentUserId) {
        List<Long> friendIds = userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getRelation, UserFriendRelation.BLACKLIST)
                .list()
                .stream()
                .map(MetaverseUserFriendDO::getFriendId)
                .collect(Collectors.toList());
        return userService.findUserInfoByUserIds(friendIds);
    }


}
