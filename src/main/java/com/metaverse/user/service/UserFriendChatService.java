package com.metaverse.user.service;

import com.metaverse.user.db.entity.MetaverseChatRecordDO;
import com.metaverse.user.db.service.IMetaverseChatRecordService;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.req.SendChatAudioReq;
import com.metaverse.user.req.SendChatRecordReq;
import com.metaverse.user.req.WithdrawChatMessageReq;
import com.metaverse.user.resp.UserFriendChatMesagesResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public Boolean withdrawChatMessages(WithdrawChatMessageReq req, Long currentUserId) {
        return metaverseChatRecordService.lambdaUpdate()
                .eq(MetaverseChatRecordDO::getSenderId, currentUserId)
                .eq(MetaverseChatRecordDO::getReceiverId, req.getReceiverId())
                .eq(MetaverseChatRecordDO::getTimestamp, req.getTimestamp())
                .eq(MetaverseChatRecordDO::getWithdrawn, Boolean.FALSE)
                .set(MetaverseChatRecordDO::getWithdrawn, Boolean.TRUE)
                .set(MetaverseChatRecordDO::getWithdrawnTime, LocalDateTime.now())
                .update();
    }
}
