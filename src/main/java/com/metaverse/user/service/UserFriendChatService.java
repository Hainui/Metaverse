package com.metaverse.user.service;

import com.metaverse.user.db.entity.MetaverseChatRecordDO;
import com.metaverse.user.db.service.IMetaverseChatRecordService;
import com.metaverse.user.req.SendChatFileReq;
import com.metaverse.user.req.SendChatRecordReq;
import com.metaverse.user.req.WithdrawChatMessageReq;
import com.metaverse.user.resp.UserFriendChatMessagesResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
    public Boolean sendChatFile(SendChatFileReq req, Long currentUserId) {
        if (!userFriendService.targetUserIsFriend(req.getReceiverId(), currentUserId)) {
            return false;
        }
        return metaverseChatRecordService.save(new MetaverseChatRecordDO()
                .setSenderId(currentUserId)
                .setReceiverId(req.getReceiverId())
                .setMessageType(Boolean.TRUE)
                .setTimestamp(LocalDateTime.now())
                .setFileId(req.getFileId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public List<UserFriendChatMessagesResp> getUserFriendChatMessages(Long friendId, Long currentUserId, Integer theOtherDay) {
        LocalDateTime minTime = LocalDateTime.of(LocalDate.now().minusDays(theOtherDay), LocalTime.MIN);
        LocalDateTime maxTime = LocalDateTime.of(LocalDate.now().minusDays(theOtherDay), LocalTime.MAX);
        List<MetaverseChatRecordDO> chatRecordDOs = metaverseChatRecordService.lambdaQuery()
                .in(MetaverseChatRecordDO::getSenderId, friendId, currentUserId)
                .in(MetaverseChatRecordDO::getReceiverId, friendId, currentUserId)
                .between(MetaverseChatRecordDO::getTimestamp, minTime, maxTime)
                .orderByAsc(MetaverseChatRecordDO::getTimestamp)
                .list();

        return chatRecordDOs.stream()
                .map(chatRecord -> new UserFriendChatMessagesResp(
                        chatRecord.getSenderId(),
                        chatRecord.getReceiverId(),
                        chatRecord.getTimestamp(),
                        chatRecord.getContent(),
                        chatRecord.getFileId(),
                        chatRecord.getWithdrawn(),
                        chatRecord.getWithdrawnTime(),
                        chatRecord.getMessageType()))
                .collect(Collectors.toList());
    }

//    private String processContent(String content) {
//        if (content.isEmpty()) {
//            return "";
//        }
//        int maxLength = 2000;
//        content = content.replaceAll("<", "&lt;")
//                .replaceAll(">", "&gt;")
//                .replaceAll("\n", "<br/>");
//        if (content.length() > maxLength) {
//            content = content.substring(0, maxLength - 3) + "...";
//        }
//        return content;
//    }

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
