package com.metaverse.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.metaverse.user.db.entity.MetaverseGroupChatRecordDO;
import com.metaverse.user.db.service.impl.MetaverseGroupChatRecordServiceImpl;
import com.metaverse.user.req.GroupChatFileReq;
import com.metaverse.user.req.GroupSendChatRecordReq;
import com.metaverse.user.req.withdrawGroupChatMessagesReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGroupChatService {

    private final UserGroupMemberService userGroupMemberService;
    private final MetaverseGroupChatRecordServiceImpl metaverseGroupChatRecordService;

    public Boolean sendChatMessages(GroupSendChatRecordReq req, Long currentUserId) {
        boolean isMember = userGroupMemberService.isUserMemberOfGroup(currentUserId, req.getGroupId());
        if (!isMember) {
            return false;
        }
        try {
            return metaverseGroupChatRecordService.save(new MetaverseGroupChatRecordDO()
                    .setGroupId(req.getGroupId())
                    .setSenderId(currentUserId)
                    .setMessageType(0)
                    .setContent(req.getContent())
                    .setTimestamp(LocalDateTime.now())
            );
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean sendGroupChatFile(GroupChatFileReq req, Long currentUserId) {
        boolean isMember = userGroupMemberService.isUserMemberOfGroup(currentUserId, req.getGroupId());
        if (!isMember) {
            return false;
        }
        try {
            return metaverseGroupChatRecordService.save(new MetaverseGroupChatRecordDO()
                    .setGroupId(req.getGroupId())
                    .setSenderId(currentUserId)
                    .setMessageType(2)
                    .setFileId(req.getFileId())
                    .setContent(req.getFileName())
                    .setTimestamp(LocalDateTime.now())
            );
        } catch (Exception e) {
            return false;
        }
    }

    public Boolean sendGroupChatAudio(GroupChatFileReq req, Long currentUserId) {
        boolean isMember = userGroupMemberService.isUserMemberOfGroup(currentUserId, req.getGroupId());
        if (!isMember) {
            return false;
        }
        try {
            return metaverseGroupChatRecordService.save(new MetaverseGroupChatRecordDO()
                    .setGroupId(req.getGroupId())
                    .setSenderId(currentUserId)
                    .setMessageType(1)
                    .setFileId(req.getFileId())
                    .setContent(req.getFileName())
                    .setTimestamp(LocalDateTime.now())
            );
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean withdrawGroupChatMessages(withdrawGroupChatMessagesReq req, Long currentUserId) {
        // 发送信息之后被踢掉就不能撤回
        boolean isMember = userGroupMemberService.isUserMemberOfGroup(currentUserId, req.getGroupId());
        if (!isMember) {
            return false;
        }

        Optional<MetaverseGroupChatRecordDO> optionalMessage = findMessageByGroupIdAndTimestamp(req.getGroupId(), req.getTimestamp());
        if (!optionalMessage.isPresent() || optionalMessage.get().getWithdrawn() != null) {
            return false;
        }
        return metaverseGroupChatRecordService.lambdaUpdate()
                .eq(MetaverseGroupChatRecordDO::getSenderId, currentUserId)
                .eq(MetaverseGroupChatRecordDO::getTimestamp, req.getTimestamp())
                .eq(MetaverseGroupChatRecordDO::getGroupId, req.getGroupId())
                .set(MetaverseGroupChatRecordDO::getWithdrawn, Boolean.TRUE)
                .set(MetaverseGroupChatRecordDO::getWithdrawnTime, LocalDateTime.now())
                .update();

    }

    public Optional<MetaverseGroupChatRecordDO> findMessageByGroupIdAndTimestamp(Long groupId, Long timestamp) {
        QueryWrapper<MetaverseGroupChatRecordDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId)
                .eq("timestamp", timestamp);
        return Optional.ofNullable(metaverseGroupChatRecordService.getOne(queryWrapper));
    }

}
