package com.metaverse.user.service;

import com.aliyuncs.exceptions.ClientException;
import com.metaverse.common.Utils.AliOSSUtils;
import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.Utils.UrlEncryptorDecryptor;
import com.metaverse.common.config.BeanManager;
import com.metaverse.file.FileIdGen;
import com.metaverse.file.db.entity.MetaverseMultimediaFilesDO;
import com.metaverse.file.db.service.IMetaverseMultimediaFilesService;
import com.metaverse.user.db.entity.MetaverseChatRecordDO;
import com.metaverse.user.db.entity.MetaverseUserFriendDO;
import com.metaverse.user.db.service.IMetaverseChatRecordService;
import com.metaverse.user.db.service.IMetaverseUserFriendService;
import com.metaverse.user.req.SendChatRecordReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserChatService {

    private final IMetaverseMultimediaFilesService metaverseMultimediaFilesService;//多媒体服务表
    private final IMetaverseChatRecordService metaverseChatRecordService;//聊天记录表
    private final IMetaverseUserFriendService userFriendService;//好友表
    private final AliOSSUtils aliOSSUtils;

    public Boolean sendChatMessages(SendChatRecordReq req, Long currentUserId, int messageType) {
        //todo 加一个判断对方有没有删除或拉黑好友
        Set<Long> blacklistId = userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, req.getReceiverId())
                .eq(MetaverseUserFriendDO::getRelation, 2)
                .list()
                .stream()
                .map(MetaverseUserFriendDO::getFriendId)
                .collect(Collectors.toSet());
        if (blacklistId.contains(currentUserId)) {
            return false;
        }
        Set<Long> statusListId = userFriendService.lambdaQuery()
                .eq(MetaverseUserFriendDO::getUserId, req.getReceiverId())
                .eq(MetaverseUserFriendDO::getStatus, 2)
                .list()
                .stream()
                .map(MetaverseUserFriendDO::getFriendId)
                .collect(Collectors.toSet());
        if (statusListId.contains(currentUserId)) {
            return false;
        }
        MetaverseChatRecordDO chatRecord = new MetaverseChatRecordDO();
        chatRecord.setSenderId(currentUserId);
        chatRecord.setReceiverId(req.getReceiverId());
        chatRecord.setMessageType(messageType);
        chatRecord.setTimestamp(LocalDateTime.now());
        String content = req.getContent();
        if (content == null || content.trim().isEmpty()) {
            content = "";
        }
        chatRecord.setContent(content);
        return metaverseChatRecordService.save(chatRecord);

    }

    public Boolean sendChatFile(Long receiverId, Byte messageType, MultipartFile file, Long currentUserId) throws IOException, ClientException {
        //todo 加一个判断对方有没有删除或拉黑好友
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

        int Type = messageType;
        MetaverseChatRecordDO chatRecord = new MetaverseChatRecordDO();
        chatRecord.setSenderId(currentUserId);
        chatRecord.setReceiverId(receiverId);
        chatRecord.setMessageType(Type);
        chatRecord.setTimestamp(LocalDateTime.now());
        if (messageType == 2 || messageType == 3) {
            String url = aliOSSUtils.upload(file);
            Long fileId = BeanManager.getBean(FileIdGen.class).nextId();
            metaverseMultimediaFilesService.save(new MetaverseMultimediaFilesDO()
                    .setUploaderId(MetaverseContextUtil.getCurrentUserId())
                    .setUploadTime(LocalDateTime.now())
                    .setId(fileId)
                    .setUrl(UrlEncryptorDecryptor.encryptUrl(url, MetaverseContextUtil.getCurrentUserRegion().getId())));
            chatRecord.setFileId(fileId);

        }
        return metaverseChatRecordService.save(chatRecord);
    }

}
