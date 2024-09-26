package com.metaverse.user.service;

import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.user.db.entity.MetaverseChatRecordDO;
import com.metaverse.user.db.service.IMetaverseChatRecordService;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.req.SendChatAudioReq;
import com.metaverse.user.req.SendChatRecordReq;
import com.metaverse.user.req.withdrawChatMessageReq;
import com.metaverse.user.resp.UserFriendChatMesagesResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserFriendChatService {

    private final IMetaverseChatRecordService metaverseChatRecordService;
    private final UserFriendService userFriendService;

    @Transactional(rollbackFor = Exception.class)
    public Boolean sendChatMessages(SendChatRecordReq req, Long currentUserId) {
        if (!userFriendService.targetUserIsFriend(req.getReceiverId(), currentUserId)) {
            return false;
        }
        return metaverseChatRecordService.save(new MetaverseChatRecordDO()
                .setSenderId(currentUserId)
                .setReceiverId(req.getReceiverId())
                .setMessageType(Boolean.FALSE)
                .setTimestamp(LocalDateTime.now())
                .setContent(req.getContent()));
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean sendChatFile(Long receiverId, Long fileId, Long currentUserId) {
        if (!userFriendService.targetUserIsFriend(receiverId, currentUserId)) {
            return false;
        }
        return metaverseChatRecordService.save(new MetaverseChatRecordDO()
                .setSenderId(currentUserId)
                .setReceiverId(receiverId)
                .setMessageType(true)
                .setTimestamp(LocalDateTime.now())
                .setFileId(fileId));
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean sendChatAudio(SendChatAudioReq req, Long currentUserId) {
        if (!userFriendService.targetUserIsFriend(req.getReceiverId(), currentUserId)) {
            return false;
        }
        return metaverseChatRecordService.save(new MetaverseChatRecordDO()
                .setSenderId(currentUserId)
                .setReceiverId(req.getReceiverId())
                .setMessageType(true)
                .setTimestamp(LocalDateTime.now())
                .setFileId(req.getFileId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public List<UserFriendChatMesagesResp> getUserFriendChatMessages(Long friendId, Long currentUserId) {
        MetaverseUser metaverseUser = MetaverseUser.readLoadAndAssertNotExist(friendId);

        List<MetaverseChatRecordDO> chatRecordDOs = metaverseChatRecordService.lambdaQuery()
                .eq(MetaverseChatRecordDO::getSenderId, metaverseUser.getId())
                .eq(MetaverseChatRecordDO::getReceiverId, currentUserId)
                .list();
        if (chatRecordDOs.isEmpty()) {
            return Collections.emptyList();
        }
        List<UserFriendChatMesagesResp> responses = chatRecordDOs.stream()
                .map(chatRecord -> {
                    String content = chatRecord.getContent();
                    String processedContent = (content != null) ? processContent(content) : "";
                    Long fileId = chatRecord.getFileId();
                    boolean isWithdrawn = chatRecord.getWithdrawn();
                    LocalDateTime withdrawnTime = isWithdrawn ? chatRecord.getWithdrawnTime() : null;
                    return new UserFriendChatMesagesResp(chatRecord.getTimestamp(), processedContent, fileId, isWithdrawn, withdrawnTime);
                })
                .collect(Collectors.toList());
        return responses;
    }

    private String processContent(String content) {
        if (content.isEmpty()) {
            return "";
        }
        int maxLength = 2000;
        content = content.replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\n", "<br/>");
        if (content.length() > maxLength) {
            content = content.substring(0, maxLength - 3) + "...";
        }
        return content;
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean withdrawChatMessages(@Valid withdrawChatMessageReq req, Long currentUserId) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(req.getTimestamp(), now);
        if (duration.toMinutes() > 2) {
            throw new IllegalArgumentException("消息发送时间超过两分钟，无法撤回");
        }

        return metaverseChatRecordService.lambdaUpdate()
                .eq(MetaverseChatRecordDO::getSenderId, currentUserId)
                .eq(MetaverseChatRecordDO::getReceiverId, req.getReceiverId())
                .eq(MetaverseChatRecordDO::getTimestamp, req.getTimestamp())
                .set(MetaverseChatRecordDO::getWithdrawn, 1)
                .set(MetaverseChatRecordDO::getWithdrawnTime, LocalDateTime.now())
                .last(RepositoryConstant.FOR_UPDATE)
                .update();
    }
}
