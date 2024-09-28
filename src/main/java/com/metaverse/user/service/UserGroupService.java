package com.metaverse.user.service;

import com.metaverse.user.db.service.IMetaverseUserGroupMemberService;
import com.metaverse.user.domain.MetaverseUserGroup;
import com.metaverse.user.req.CreateUserGroupReq;
import com.metaverse.user.req.ModifyUserGroupReq;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserGroupService {

    private final IMetaverseUserGroupMemberService userGroupMemberService;

    public Long createUserGroup(Long currentUserId, CreateUserGroupReq req) {
        return MetaverseUserGroup.create(req.getGroupName(), req.getDescription(), currentUserId, Optional.of(req.getMemberIds()).orElse(Collections.emptyList()));
    }

    public boolean modifyUserGroup(Long currentUserId, ModifyUserGroupReq req) {
        MetaverseUserGroup metaverseUserGroup = MetaverseUserGroup.writeLoadAndAssertNotExist(req.getUserGroupId());
        return metaverseUserGroup.modifyGroupInfo(req.getGroupName(), req.getDescription(), currentUserId);
    }
}
