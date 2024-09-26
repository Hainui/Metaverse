package com.metaverse.user.service;

import com.aliyuncs.exceptions.ClientException;
import com.metaverse.common.Utils.AliOSSUtils;
import com.metaverse.file.db.service.IMetaverseMultimediaFilesService;
import com.metaverse.user.db.entity.MetaverseChatRecordDO;
import com.metaverse.user.db.service.IMetaverseChatRecordService;
import com.metaverse.user.db.service.IMetaverseUserFriendService;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.req.SendChatAudioReq;
import com.metaverse.user.req.SendChatRecordReq;
import com.metaverse.user.resp.UserFriendChatMesagesResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserChatService {

    private final IMetaverseMultimediaFilesService metaverseMultimediaFilesService;//多媒体服务表
    private final IMetaverseChatRecordService metaverseChatRecordService;//聊天记录表
    private final IMetaverseUserFriendService MetaverseUserFriendService;//好友表
    private final UserFriendService userFriendService;
    private final AliOSSUtils aliOSSUtils;

    @Transactional(rollbackFor = Exception.class)
    public Boolean sendChatMessages(SendChatRecordReq req, Long currentUserId) {
        if (!userFriendService.checkBlacklistAndStatusList(req.getReceiverId(), currentUserId)) {
            return false;
        }
        MetaverseChatRecordDO chatRecord = new MetaverseChatRecordDO()
                .setSenderId(currentUserId)
                .setReceiverId(req.getReceiverId())
                .setMessageType(false)
                .setTimestamp(LocalDateTime.now())
                .setContent(req.getContent());
        return metaverseChatRecordService.save(chatRecord);
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean sendChatFile(Long receiverId, Boolean messageType, Long fileId, Long currentUserId) throws IOException, ClientException {
        if (!userFriendService.checkBlacklistAndStatusList(receiverId, currentUserId)) {
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
    public Boolean sendChatAudio(@Valid SendChatAudioReq req, Long currentUserId) {
        if (!userFriendService.checkBlacklistAndStatusList(req.getReceiverId(), currentUserId)) {
            return false;
        }
        MetaverseChatRecordDO chatRecord = new MetaverseChatRecordDO()
                .setSenderId(currentUserId)
                .setReceiverId(req.getReceiverId())
                .setMessageType(false)
                .setTimestamp(LocalDateTime.now())
                .setFileId(req.getFileId());
        return metaverseChatRecordService.save(chatRecord);
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
                    boolean isWithdrawn = chatRecord.getWithdrawn();
                    LocalDateTime withdrawnTime = isWithdrawn ? chatRecord.getWithdrawnTime() : null;
                    return new UserFriendChatMesagesResp(chatRecord.getTimestamp(), processedContent, isWithdrawn, withdrawnTime);
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

}
