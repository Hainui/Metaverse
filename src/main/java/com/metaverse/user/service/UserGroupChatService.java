package com.metaverse.user.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.metaverse.file.dto.FileDto;
import com.metaverse.user.db.entity.MetaverseGroupChatRecordDO;
import com.metaverse.user.db.service.impl.MetaverseGroupChatRecordServiceImpl;
import com.metaverse.user.req.GroupChatFileReq;
import com.metaverse.user.req.GroupSendChatRecordReq;
import com.metaverse.user.req.withdrawGroupChatMessagesReq;
import com.metaverse.user.resp.GroupChatMessagesResp;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGroupChatService {

    private final UserGroupMemberService userGroupMemberService;
    private final MetaverseGroupChatRecordServiceImpl metaverseGroupChatRecordService;

    public Boolean sendChatMessages(GroupSendChatRecordReq req, Long currentUserId) {
        if (!userGroupMemberService.isUserMemberOfGroup(currentUserId, req.getGroupId())) {
            throw new IllegalArgumentException("该用户不是本群成员,无法发送信息");
        }
        return metaverseGroupChatRecordService.save(new MetaverseGroupChatRecordDO()
                .setGroupId(req.getGroupId())
                .setSenderId(currentUserId)
                .setMessageType(0)
                .setContent(req.getContent())
                .setTimestamp(LocalDateTime.now())
        );
    }

    public Boolean sendGroupChatFile(GroupChatFileReq req, Long currentUserId) {
        if (!userGroupMemberService.isUserMemberOfGroup(currentUserId, req.getGroupId())) {
            throw new IllegalArgumentException("该用户不是本群成员,无法发送信息");
        }
        return metaverseGroupChatRecordService.save(new MetaverseGroupChatRecordDO()
                .setGroupId(req.getGroupId())
                .setSenderId(currentUserId)
                .setMessageType(2)
                .setFileId(req.getFileId())
                .setContent(req.getFileName())
                .setTimestamp(LocalDateTime.now())
        );
    }

    public Boolean sendGroupChatAudio(GroupChatFileReq req, Long currentUserId) {
        if (!userGroupMemberService.isUserMemberOfGroup(currentUserId, req.getGroupId())) {
            throw new IllegalArgumentException("该用户不是本群成员,无法发送信息");
        }
        return metaverseGroupChatRecordService.save(new MetaverseGroupChatRecordDO()
                .setGroupId(req.getGroupId())
                .setSenderId(currentUserId)
                .setMessageType(1)
                .setFileId(req.getFileId())
                .setTimestamp(LocalDateTime.now())
        );
    }

    @Transactional(rollbackFor = Exception.class)
    public Boolean withdrawGroupChatMessages(withdrawGroupChatMessagesReq req, Long currentUserId) {
        if (!userGroupMemberService.isUserMemberOfGroup(currentUserId, req.getGroupId())) {
            throw new IllegalArgumentException("该用户不是本群成员,无法撤回");
        }

        return metaverseGroupChatRecordService.lambdaUpdate()
                .eq(MetaverseGroupChatRecordDO::getSenderId, currentUserId)
                .eq(MetaverseGroupChatRecordDO::getTimestamp, req.getTimestamp())
                .eq(MetaverseGroupChatRecordDO::getGroupId, req.getGroupId())
                .eq(MetaverseGroupChatRecordDO::getWithdrawn, Boolean.FALSE)
                .set(MetaverseGroupChatRecordDO::getWithdrawn, Boolean.TRUE)
                .set(MetaverseGroupChatRecordDO::getWithdrawnTime, LocalDateTime.now())
                .update();

    }

    public Optional<MetaverseGroupChatRecordDO> findMessageByGroupIdAndTimestamp(Long groupId, LocalDateTime timestamp) {
        QueryWrapper<MetaverseGroupChatRecordDO> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id", groupId)
                .eq("timestamp", timestamp);
        return Optional.ofNullable(metaverseGroupChatRecordService.getOne(queryWrapper));
    }

    public List<FileDto> getGroupChatFile(Long groupId, Long currentUserId) {
        if (!userGroupMemberService.isUserMemberOfGroup(currentUserId, groupId)) {
            throw new IllegalArgumentException("该用户不是本群成员,无法获取文件");
        }
        return metaverseGroupChatRecordService.lambdaQuery()
                .eq(MetaverseGroupChatRecordDO::getGroupId, groupId)
                .eq(MetaverseGroupChatRecordDO::getMessageType, 2)
                .list().stream().map(this::convertToFileDto).collect(Collectors.toList());
    }

    private FileDto convertToFileDto(MetaverseGroupChatRecordDO metaverseChatRecordDO) {
        if (metaverseChatRecordDO == null) {
            return null;
        }
        return new FileDto()
                .setFileName(metaverseChatRecordDO.getContent())
                .setFileId(metaverseChatRecordDO.getFileId());
    }

    public List<GroupChatMessagesResp> getGroupChatMessages(Long groupId, Long currentUserId, Integer theOtherDay) {
        if (!userGroupMemberService.isUserMemberOfGroup(currentUserId, groupId)) {
            throw new IllegalArgumentException("该用户不是本群成员,无法获取聊天信息");
        }
        LocalDateTime minTime = LocalDateTime.of(LocalDate.now().minusDays(theOtherDay), LocalTime.MIN);
        LocalDateTime maxTime = LocalDateTime.of(LocalDate.now().minusDays(theOtherDay), LocalTime.MAX);
        log.info("开始查询群组聊天消息,group ID: {}, user ID: {}, from {} to {}", groupId, currentUserId, minTime, maxTime);
        List<MetaverseGroupChatRecordDO> chatRecordDOs = metaverseGroupChatRecordService.lambdaQuery()
                .eq(MetaverseGroupChatRecordDO::getGroupId, groupId)
                .between(MetaverseGroupChatRecordDO::getTimestamp, minTime, maxTime)
                .orderByAsc(MetaverseGroupChatRecordDO::getTimestamp)
                .list();
        log.info("查询群组聊天信息结束 group ID: {}, user ID: {}", groupId, currentUserId);
        return chatRecordDOs.stream()
                .map(chatRecord -> new GroupChatMessagesResp(
                        chatRecord.getSenderId(),
                        chatRecord.getTimestamp(),
                        chatRecord.getMessageType(),
                        chatRecord.getContent(),
                        chatRecord.getFileId(),
                        chatRecord.getWithdrawn(),
                        chatRecord.getWithdrawnTime()))
                .collect(Collectors.toList());

    }
}
