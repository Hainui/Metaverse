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
import com.metaverse.user.db.service.IMetaverseChatRecordService;
import com.metaverse.user.db.service.IMetaverseUserFriendService;
import com.metaverse.user.req.SendChatAudioReq;
import com.metaverse.user.req.SendChatRecordReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserChatService {

    private final IMetaverseMultimediaFilesService metaverseMultimediaFilesService;//多媒体服务表
    private final IMetaverseChatRecordService metaverseChatRecordService;//聊天记录表
    private final IMetaverseUserFriendService MetaverseUserFriendService;//好友表
    private final UserFriendService userFriendService;
    private final AliOSSUtils aliOSSUtils;


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

    public Boolean sendChatFile(Long receiverId, Byte messageType, MultipartFile file, Long currentUserId) throws IOException, ClientException {
        if (!userFriendService.checkBlacklistAndStatusList(receiverId, currentUserId)) {
            return false;
        }
        String url = aliOSSUtils.upload(file);
        Long fileId = BeanManager.getBean(FileIdGen.class).nextId();
        metaverseMultimediaFilesService.save(new MetaverseMultimediaFilesDO()
                .setUploaderId(MetaverseContextUtil.getCurrentUserId())
                .setUploadTime(LocalDateTime.now())
                .setId(fileId)
                .setUrl(UrlEncryptorDecryptor.encryptUrl(url, MetaverseContextUtil.getCurrentUserRegion().getId())));
        return metaverseChatRecordService.save(new MetaverseChatRecordDO()
                .setSenderId(currentUserId)
                .setReceiverId(receiverId)
                .setMessageType(true)
                .setTimestamp(LocalDateTime.now())
                .setFileId(fileId));

    }

    public Boolean sendChatAudio(@Valid SendChatAudioReq req, Long currentUserId) {
        if (!userFriendService.checkBlacklistAndStatusList(req.getReceiverId(), currentUserId)) {
            return false;
        }
        MetaverseChatRecordDO chatRecord = new MetaverseChatRecordDO()
                .setSenderId(currentUserId)
                .setReceiverId(req.getReceiverId())
                .setMessageType(false)
                .setTimestamp(LocalDateTime.now())
                .setFileId(req.getAudioId());
        return metaverseChatRecordService.save(chatRecord);
    }
}
