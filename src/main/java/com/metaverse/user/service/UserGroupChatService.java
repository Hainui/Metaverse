package com.metaverse.user.service;

import com.metaverse.user.req.GroupSendChatRecordReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGroupChatService {

    private final UserGroupMemberService userGroupMemberService;

    public Boolean sendChatMessages(GroupSendChatRecordReq req, Long currentUserId) {
        return null;
    }
}
