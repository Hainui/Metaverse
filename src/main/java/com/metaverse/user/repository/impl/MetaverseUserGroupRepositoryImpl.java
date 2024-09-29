package com.metaverse.user.repository.impl;

import com.metaverse.common.constant.RepositoryConstant;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.db.entity.MetaverseUserGroupDO;
import com.metaverse.user.db.entity.MetaverseUserGroupMemberDO;
import com.metaverse.user.db.service.IMetaverseUserGroupMemberService;
import com.metaverse.user.db.service.IMetaverseUserGroupService;
import com.metaverse.user.db.service.IMetaverseUserService;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.domain.MetaverseUserGroup;
import com.metaverse.user.dto.UserGroupMemberInfo;
import com.metaverse.user.repository.MetaverseUserGroupRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class MetaverseUserGroupRepositoryImpl implements MetaverseUserGroupRepository {
    private final IMetaverseUserGroupService userGroupService;
    private final IMetaverseUserGroupMemberService userGroupMemberService;
    private final IMetaverseUserService userService;

    @Override
    public MetaverseUserGroup findByIdWithWriteLock(Long id) {
        MetaverseUserGroupDO userDO = userGroupService.lambdaQuery()
                .eq(MetaverseUserGroupDO::getId, id)
                .last(RepositoryConstant.FOR_UPDATE)
                .one();
        return userGroupDOConvertToUserGroup(userDO);
    }

    @Override
    public MetaverseUserGroup findByIdWithReadLock(Long id) {
        MetaverseUserGroupDO userDO = userGroupService.lambdaQuery()
                .eq(MetaverseUserGroupDO::getId, id)
                .last(RepositoryConstant.FOR_SHARE)
                .one();
        return userGroupDOConvertToUserGroup(userDO);
    }

    @Override
    public boolean save(MetaverseUserGroupDO metaverseUserGroupDO, List<Long> memberIds) {
        LocalDateTime now = LocalDateTime.now();
        try {
            memberIds.forEach(memberId ->
                    userGroupMemberService.save(new MetaverseUserGroupMemberDO()
                            .setGroupId(metaverseUserGroupDO.getId())
                            .setRole(0)
                            .setJoinedAt(now)
                            .setMemberId(memberId)
                            .setVersion(0L)));
            userGroupMemberService.save(new MetaverseUserGroupMemberDO()
                    .setGroupId(metaverseUserGroupDO.getId())
                    .setRole(2)
                    .setJoinedAt(now)
                    .setMemberId(metaverseUserGroupDO.getCreatorId())
                    .setVersion(0L));
            return userGroupService.save(metaverseUserGroupDO);
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException("存在重复的数据！");
        }
    }

    @Override
    public boolean modifyGroupInfo(Long groupId, String groupName, String description, Long newVersion, Long currentUserId) {
        return userGroupService.lambdaUpdate()
                .eq(MetaverseUserGroupDO::getId, groupId)
                .set(MetaverseUserGroupDO::getGroupName, groupName)
                .set(MetaverseUserGroupDO::getDescription, description)
                .set(MetaverseUserGroupDO::getUpdateBy, currentUserId)
                .set(MetaverseUserGroupDO::getVersion, newVersion)
                .update();
    }

    private MetaverseUserGroup userGroupDOConvertToUserGroup(MetaverseUserGroupDO userGroupDO) {
        if (userGroupDO == null) {
            return null;
        }
        Long groupId = userGroupDO.getId();
        List<MetaverseUserGroupMemberDO> groupMemberDOs = userGroupMemberService.lambdaQuery()
                .eq(MetaverseUserGroupMemberDO::getGroupId, groupId)
                .list();
        return new MetaverseUserGroup()
                .setId(groupId)
                .setGroupName(userGroupDO.getGroupName())
                .setCreatedAt(userGroupDO.getCreatedAt())
                .setCreatorId(userGroupDO.getCreatorId())
                .setDescription(userGroupDO.getDescription())
                .setMemberInfoList(groupMemberDOs.stream().map(this::convertToMemberInfo).collect(Collectors.toList()))
                .setVersion(userGroupDO.getVersion());
    }

    private UserGroupMemberInfo convertToMemberInfo(MetaverseUserGroupMemberDO memberDO) {
        if (memberDO == null) {
            return null;
        }
        Long userId = memberDO.getMemberId();
        MetaverseUserDO userDO = userService.getById(userId);
        return new UserGroupMemberInfo()
                .setUserId(userId)
                .setBirthTime(userDO.getBirthTime())
                .setGender(MetaverseUser.Gender.convertGender(userDO.getGender()))
                .setName(userDO.getUsername())
                .setAvatarImageId(userDO.getAvatarFileId())
                .setRole(memberDO.getRole())
                .setJoinedAt(memberDO.getJoinedAt());
    }
}
