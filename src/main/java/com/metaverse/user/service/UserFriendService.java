package com.metaverse.user.service;

import cn.hutool.core.util.StrUtil;
import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.user.db.entity.*;
import com.metaverse.user.db.service.*;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.req.AddFriendReq;
import com.metaverse.user.req.AnswerUserQuestionReq;
import com.metaverse.user.resp.MetaverseFriendRequestResp;
import com.metaverse.user.resp.MetaverseUserAbstractInfo;
import com.metaverse.user.resp.UserFriendQuestionResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFriendService {

    private final IMetaverseFriendRequestService friendRequestService;
    private final IMetaverseUserFriendService userFriendService;
    private final IMetaverseUserService userService;
    private final IMetaverseUserFriendQuestionService userFriendQuestionService;
    private final IMetaverseUserFriendOperationLogService userFriendOperationLogService;

    public boolean checkBlacklistAndStatusList(Long receiverId, Long currentUserId) {
        Set<Long> blacklistId = userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, receiverId)
                .eq(MetaverseUserFriendDO::getRelation, 2)
                .list()
                .stream()
                .map(MetaverseUserFriendDO::getFriendId)
                .collect(Collectors.toSet());
        if (blacklistId.contains(currentUserId)) {
            return false;
        }
        Set<Long> statusListId = userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, receiverId)
                .eq(MetaverseUserFriendDO::getStatus, 2)
                .list()
                .stream()
                .map(MetaverseUserFriendDO::getFriendId)
                .collect(Collectors.toSet());
        if (statusListId.contains(currentUserId)) {
            return false;
        }
        return true;
    }

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
                .eq(MetaverseFriendRequestDO::getStatus, 0)
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
        return convertToQuestionResp(userFriendQuestionService.lambdaQuery().eq(MetaverseUserFriendQuestionDO::getUserId, receiverId).one());
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
        MetaverseFriendRequestDO one = assertNotExistWriteLoadFriendRequest(currentUserId, senderId);
        friendRequestService.lambdaUpdate()
                .eq(MetaverseFriendRequestDO::getSenderId, senderId)
                .eq(MetaverseFriendRequestDO::getReceiverId, currentUserId)
                .eq(MetaverseFriendRequestDO::getStatus, 0)
                .set(MetaverseFriendRequestDO::getStatus, 1)
                .set(MetaverseFriendRequestDO::getUpdateBy, currentUserId)
                .set(MetaverseFriendRequestDO::getVersion, one.getVersion() + 1)
                .update();

        LocalDateTime now = LocalDateTime.now();

        MetaverseUserFriendDO userFriendDO = userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getFriendId, senderId)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();

        if (userFriendDO == null) {
            userFriendService.save(new MetaverseUserFriendDO()
                    .setUserId(currentUserId)
                    .setFriendId(senderId)
                    .setVersion(0L)
                    .setCreatedAt(now)
                    .setIntimacyLevel(new BigDecimal(0))
                    .setStatus(1)
                    .setRelation(1));
        } else {
            userFriendService.lambdaUpdate()
                    .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                    .eq(MetaverseUserFriendDO::getFriendId, senderId)
                    .set(MetaverseUserFriendDO::getStatus, 1)
                    .set(MetaverseUserFriendDO::getRelation, 1)
                    .set(MetaverseUserFriendDO::getUpdateBy, currentUserId)
                    .set(MetaverseUserFriendDO::getVersion, userFriendDO.getVersion() + 1)
                    .set(MetaverseUserFriendDO::getIntimacyLevel, new BigDecimal(0))
                    .update();
        }
        saveUserFriendOperationLog(currentUserId, senderId, now, 1);
        return true;
    }

    @NotNull
    private MetaverseFriendRequestDO assertNotExistWriteLoadFriendRequest(Long currentUserId, Long senderId) {
        MetaverseFriendRequestDO one = friendRequestService.lambdaQuery()
                .eq(MetaverseFriendRequestDO::getSenderId, senderId)
                .eq(MetaverseFriendRequestDO::getReceiverId, currentUserId)
                .eq(MetaverseFriendRequestDO::getStatus, 0)
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
        MetaverseUserFriendQuestionDO userQuestion = userFriendQuestionService.getById(receiverId);
        if (userQuestion.getEnabled() && StrUtil.equals(req.getQuestionAnswer(), userQuestion.getCorrectAnswer())) {
            return agreeFriendRequest(receiverId, currentUserId);
        }
        return false;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean delFriend(Long targetId, Long currentUserId) {
        MetaverseUserFriendDO userFriendDO = assertNotExistAndWriteLoadUserFriend(currentUserId, targetId);
        boolean noChange = userFriendDO.getStatus().equals(2);
        if (noChange) {
            return false;
        }
        userFriendService.lambdaUpdate()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getFriendId, targetId)
                .set(MetaverseUserFriendDO::getStatus, 2)
                .set(MetaverseUserFriendDO::getUpdateBy, currentUserId)
                .set(MetaverseUserFriendDO::getVersion, userFriendDO.getVersion() + 1)
                .set(MetaverseUserFriendDO::getIntimacyLevel, new BigDecimal(0))
                .update();
        saveUserFriendOperationLog(currentUserId, targetId, LocalDateTime.now(), 2);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean blockFriend(Long targetId, Long currentUserId) {
        MetaverseUserFriendDO userFriendDO = assertNotExistAndWriteLoadUserFriend(currentUserId, targetId);
        boolean noChange = userFriendDO.getRelation().equals(2);
        if (noChange) {
            return false;
        }
        userFriendService.lambdaUpdate()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getFriendId, targetId)
                .set(MetaverseUserFriendDO::getRelation, 2)
                .set(MetaverseUserFriendDO::getUpdateBy, currentUserId)
                .set(MetaverseUserFriendDO::getVersion, userFriendDO.getVersion() + 1)
                .set(MetaverseUserFriendDO::getIntimacyLevel, new BigDecimal(0))
                .update();
        saveUserFriendOperationLog(currentUserId, targetId, LocalDateTime.now(), 3);
        return true;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean unblockFriends(Long targetId, Long currentUserId) {
        MetaverseUserFriendDO userFriendDO = assertNotExistAndWriteLoadUserFriend(currentUserId, targetId);
        boolean noChange = userFriendDO.getRelation().equals(1);
        if (noChange) {
            return false;
        }
        userFriendService.lambdaUpdate()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getFriendId, targetId)
                .set(MetaverseUserFriendDO::getRelation, 1)
                .set(MetaverseUserFriendDO::getUpdateBy, currentUserId)
                .set(MetaverseUserFriendDO::getVersion, userFriendDO.getVersion() + 1)
                .set(MetaverseUserFriendDO::getIntimacyLevel, new BigDecimal(0))
                .update();
        saveUserFriendOperationLog(currentUserId, targetId, LocalDateTime.now(), 4);
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
                .eq(MetaverseUserFriendDO::getStatus, 1)
                .eq(MetaverseUserFriendDO::getRelation, 1)
                .exists();
    }


    public static class UserFriendRelation {
        public static final int FRIEND = 1;
        public static final int BLACKLIST = 2;
    }

    public static class UserFriendStatus {
        public static final int NORMAL = 1;
        public static final int DELETED = 2;
    }

    public List<MetaverseUserAbstractInfo> getAllFriend(Long currentUserId) {
        List<Long> friendIds = userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, currentUserId)
                .eq(MetaverseUserFriendDO::getStatus, 1)
                .eq(MetaverseUserFriendDO::getRelation, 1)
                .list()
                .stream()
                .map(MetaverseUserFriendDO::getFriendId)
                .collect(Collectors.toList());
//        userService.lambdaUpdate().eq


//        List<MetaverseFriendListResp> friendListResp = new ArrayList<>();
//        for (MetaverseUserFriendDO userFriend : userFriendList) {
//            MetaverseUserDO user = userService.getById(userFriend.getFriendId());
//            if (user != null) {
//                MetaverseFriendListResp resp = getMetaverseFriendListResp(userFriend, user);
//                friendListResp.add(resp);
//            }
//        }
//        return friendListResp;
        return null;
    }


}
