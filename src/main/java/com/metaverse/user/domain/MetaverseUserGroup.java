package com.metaverse.user.domain;

import com.metaverse.common.config.BeanManager;
import com.metaverse.common.model.IAggregateRoot;
import com.metaverse.user.UserGroupIdGen;
import com.metaverse.user.db.entity.MetaverseUserGroupDO;
import com.metaverse.user.dto.UserGroupMemberInfo;
import com.metaverse.user.repository.MetaverseUserGroupRepository;
import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class MetaverseUserGroup implements IAggregateRoot<MetaverseUser> {

    protected static final Long MODEL_VERSION = 1L;

    /**
     * 群组ID
     */
    private Long id;
    /**
     * 群组名称
     */
    private String groupName;
    /**
     * 群组描述
     */
    private String description;
    /**
     * 创建群组的用户ID
     */
    private Long creatorId;
    /**
     * 群组创建时间
     */
    private LocalDateTime createdAt;
    /**
     * 群成员信息
     */
    private List<UserGroupMemberInfo> memberInfoList;

    private Long version;

    public static Long create(String groupName, String description, Long creatorId, List<Long> memberIds) {
        UserGroupIdGen idGen = BeanManager.getBean(UserGroupIdGen.class);
        long userGroupId = idGen.nextId();
        MetaverseUserGroupRepository repository = BeanManager.getBean(MetaverseUserGroupRepository.class);
        repository.save(new MetaverseUserGroupDO()
                .setId(userGroupId)
                .setCreatorId(creatorId)
                .setGroupName(groupName)
                .setDescription(description)
                .setCreatedAt(LocalDateTime.now())
                .setVersion(0L), memberIds);
        return userGroupId;
    }

    public boolean modifyGroupInfo(String groupName, String description, Long currentUserId) {
        boolean noChange = this.groupName.equals(groupName) && this.description.equals(description);
        if (noChange) {
            return false;
        }
        MetaverseUserGroupRepository repository = BeanManager.getBean(MetaverseUserGroupRepository.class);
        return repository.modifyGroupInfo(pkVal(), groupName, description, changeVersion(), currentUserId);
    }

    @NotNull
    public static MetaverseUserGroup writeLoadAndAssertNotExist(Long id) {
        MetaverseUserGroupRepository repository = BeanManager.getBean(MetaverseUserGroupRepository.class);
        MetaverseUserGroup userGroup = repository.findByIdWithWriteLock(id);
        if (Objects.isNull(userGroup)) {
            throw new IllegalArgumentException("未找到该群组信息");
        }
        return userGroup;
    }

    @NotNull
    public static MetaverseUserGroup readLoadAndAssertNotExist(Long userId) {
        MetaverseUserGroupRepository repository = BeanManager.getBean(MetaverseUserGroupRepository.class);
        MetaverseUserGroup userGroup = repository.findByIdWithReadLock(userId);
        if (Objects.isNull(userGroup)) {
            throw new IllegalArgumentException("未找到该用户信息");
        }
        return userGroup;
    }

    @Override
    public Long pkVal() {
        return id;
    }

    @Override
    public Long modelVersion() {
        return MODEL_VERSION;
    }

    @Override
    public Long changeVersion() {
        return ++version;
    }
}
