package com.metaverse.user.domain;

import cn.hutool.core.collection.CollectionUtil;
import com.metaverse.common.Utils.BCryptUtil;
import com.metaverse.common.config.BeanManager;
import com.metaverse.common.constant.UserConstant;
import com.metaverse.common.model.IAggregateRoot;
import com.metaverse.permission.domain.MetaversePermission;
import com.metaverse.permission.dto.MetaverseUserPermissionInfo;
import com.metaverse.region.domain.MetaverseRegion;
import com.metaverse.region.dto.MetaverseRegionInfo;
import com.metaverse.region.repository.MetaverseRegionRepository;
import com.metaverse.user.UserIdGen;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.dto.MetaverseUserInfo;
import com.metaverse.user.repository.MetaverseUserRepository;
import com.metaverse.user.req.MetaverseUserModifyPasswordReq;
import com.metaverse.user.req.ModifyUserNameReq;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class MetaverseUser implements IAggregateRoot<MetaverseUser> {

    protected static final Long MODEL_VERSION = 1L;

    /**
     * 身份证编号,唯一标识id
     */
    private Long id;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 姓名
     */
    private String name;
    /**
     * 密码
     */
    private String password;
    /**
     * 所在区服
     */
    private MetaverseRegion region;
    /**
     * 出生时间
     */
    private LocalDateTime birthTime;
    /**
     * 性别
     */
    private Gender gender;
    /**
     * 权限集合
     */
    private List<MetaversePermission> permissions;

    private Long updatedBy;

    private LocalDateTime updatedAt;

    private Long version;

    public static MetaverseUser writeLoadAndAssertNotExist(Long userId, Long regionId) {
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
        MetaverseUser user = repository.findByIdWithWriteLock(userId);
        if (Objects.isNull(user) || regionId != null && !regionId.equals(user.getRegion().getId())) {
            throw new IllegalArgumentException("未找到该用户信息");
        }
        return user;
    }

    /**
     * 读锁加载用户信息，提供管理员使用，暂时无需对分区加限制
     *
     * @param userId 用户id
     * @return 用户信息
     */
    public static MetaverseUser readLoadAndAssertNotExist(Long userId) {
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
        MetaverseUser user = repository.findByIdWithReadLock(userId);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("未找到该用户信息");
        }
        return user;
    }

    /**
     * 读锁加载用户信息，提供管理员使用，暂时无需对分区加限制
     *
     * @param userIds 用户ids
     * @return 用户信息
     */
    public static List<MetaverseUser> readLoadAndAssertNotExist(List<Long> userIds) {
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
        List<MetaverseUser> users = repository.findByIdsWithReadLock(userIds);
        if (CollectionUtil.isEmpty(users)) {
            throw new IllegalArgumentException("未找到该用户信息");
        }
        return users;
    }

    /**
     * 对分区有限制
     *
     * @param userId   用户id
     * @param regionId 分区id
     * @return 用户信息
     */
    public static MetaverseUser readLoadAndAssertNotExist(Long userId, Long regionId) {
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
        MetaverseUser user = repository.findByIdWithReadLock(userId);
        if (Objects.isNull(user) || regionId != null && !regionId.equals(user.getRegion().getId())) {
            throw new IllegalArgumentException("未找到该用户信息");
        }
        return user;
    }


    public static boolean registration(String name, String email, String password, Long regionId, Gender gender) {
        UserIdGen idGen = BeanManager.getBean(UserIdGen.class);
        MetaverseUserRepository userRepository = BeanManager.getBean(MetaverseUserRepository.class);
        MetaverseRegionRepository regionRepository = BeanManager.getBean(MetaverseRegionRepository.class);
        if (!regionRepository.existByRegionId(regionId)) {
            throw new IllegalArgumentException("非法的区服！");
        }
        if (userRepository.existByName(name, regionId)) {
            throw new IllegalArgumentException("该用户名已经存在！");
        }
        MetaverseUserDO entity = new MetaverseUserDO()
                .setId(idGen.nextId())
                .setEmail(email)
                .setGender(gender.getBooleanValue())
                .setPassword(BCryptUtil.hashPassword(password))
                .setRegionId(regionId).setUsername(name)
                .setUsername(name)
                .setBirthTime(LocalDateTime.now())
                .setUpdateBy(-1L)
                .setVersion(0L);
        return userRepository.save(entity);
    }


    public static MetaverseUserInfo login(String email, String password, Long regionId) {
        MetaverseRegionRepository regionRepository = BeanManager.getBean(MetaverseRegionRepository.class);
        if (!regionRepository.existByRegionId(regionId) && !UserConstant.SUPER_ADMINISTRATOR_REGION_ID.equals(regionId)) {
            throw new IllegalArgumentException("非法的区服！");
        }
        MetaverseUserRepository userRepository = BeanManager.getBean(MetaverseUserRepository.class);
        return convertToUserInfo(userRepository.login(email, password, regionId));
    }

    private static MetaverseUserInfo convertToUserInfo(MetaverseUser user) {
        if (Objects.isNull(user)) {
            return null;
        }
        return new MetaverseUserInfo()
                .setId(user.getId())
                .setEmail(user.getEmail())
                .setName(user.getName())
                .setRegion(convertToRegionInfo(user.getRegion()))
                .setPermissions(Optional.of(user.getPermissions()).orElse(Collections.emptyList()).stream().map(MetaverseUser::convertToUserPermissionInfo).collect(Collectors.toList()))
                .setGender(user.getGender().getBooleanValue());
    }

    private static MetaverseUserPermissionInfo convertToUserPermissionInfo(MetaversePermission permission) {
        if (Objects.isNull(permission)) {
            return null;
        }
        return new MetaverseUserPermissionInfo()
                .setPermissions(permission.getPermissions())
                .setPermissionGroupName(permission.getPermissionGroupName())
                .setCreateBy(permission.getCreateBy())
                .setUpdateBy(permission.getUpdatedBy());
    }

    private static MetaverseRegionInfo convertToRegionInfo(MetaverseRegion region) {
        if (Objects.isNull(region)) {
            return null;
        }
        return new MetaverseRegionInfo()
                .setServerLocation(region.getServerLocation())
                .setName(region.getName())
                .setId(region.getId());
    }

    public Boolean modifyUserName(ModifyUserNameReq req, Long currentUserId) {
        if (StringUtils.equals(name, req.getName())) {
            throw new IllegalArgumentException("修改前名字不能和原来名字相同");
        }
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
        if (repository.existByName(req.getName(), req.getRegionId())) {
            throw new IllegalArgumentException("名字已经存在");
        }
        Long newVersion = changeVersion();
        return repository.modifyUserName(req.getUserId(), req.getName(), currentUserId, newVersion);
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

    public Boolean modifyPassword(MetaverseUserModifyPasswordReq req, Long currentUserId) {
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
        if (BCryptUtil.checkPassword(req.getNewPassword(), this.password)) {
            throw new IllegalArgumentException("新密码不能与旧密码相同");
        }
        Long newVersion = changeVersion();
        return repository.modifyPassword(req.getNewPassword(), req.getUserId(), currentUserId, newVersion);
    }


    @Getter
    public enum Gender implements Serializable {
        // 定义枚举常量 FEMALE 和 MALE
        FEMALE(0, "女"),
        MALE(1, "男");

        private final int value;
        private final String description;

        // 构造方法，传入性别的整数值和描述
        Gender(int value, String description) {
            this.value = value;
            this.description = description;
        }

        // 静态方法，根据整数值反查对应的枚举值
        public static Gender fromValue(int value) {
            for (Gender gender : values()) {
                if (gender.getValue() == value) {
                    return gender;
                }
            }
            throw new IllegalArgumentException("Invalid gender value: " + value);
        }

        public static Gender convertGender(boolean gender) {
            return gender ? MALE : FEMALE;
        }

        public Boolean getBooleanValue() {
            return value == 0 ? Boolean.FALSE : Boolean.TRUE;
        }


        // 可选：重写toString方法，使得枚举常量可以直接被转换成字符串
        @Override
        public String toString() {
            return this.description;
        }
    }


}
