package com.metaverse.user.domain;

import com.metaverse.common.Utils.BCryptUtil;
import com.metaverse.common.Utils.BeanManager;
import com.metaverse.user.UserIdGen;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.repository.MetaverseUserRepository;
import com.metaverse.user.req.ModifyUserNameReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MetaverseUser {
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
     * 区服id
     */
    private Long regionId;
    /**
     * 出生时间
     */
    private LocalDateTime birthTime;
    /**
     * 性别
     */
    private Gender gender;

    private Long updatedBy;

    private LocalDateTime updatedAt;

    private Long version;

    public static MetaverseUser loadAndAssertNotExist(Long userId) {
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
        MetaverseUser user = repository.findByIdWithLock(userId);
        if (Objects.isNull(user)) {
            throw new IllegalArgumentException("未找到该用户信息");
        }
        return user;
    }


    public static boolean registration(String name, String email, String password, Long regionId, Gender gender) {
        UserIdGen idGen = BeanManager.getBean(UserIdGen.class);
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
        if (!repository.existByRegionId(regionId)) {
            throw new IllegalArgumentException("非法的区服！");
        }
        if (repository.existByName(name, regionId)) {
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
        return repository.save(entity);
    }


    public static Long login(String email, String password, Long regionId) {
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
        return repository.login(email, password, regionId);
    }

    public Boolean modifyUserName(ModifyUserNameReq req, Long currentUserId) {
        if (StringUtils.equals(name, req.getName())) {
            throw new IllegalArgumentException("修改前名字不能和原来名字相同");
        }
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
        if (repository.existByName(req.getName(), req.getRegionId())) {
            throw new IllegalArgumentException("名字已经存在");
        }
        Long newVersion = version + 1;
        return repository.modifyUserName(req.getUserId(), req.getName(), currentUserId, newVersion);
    }


    @Getter
    public enum Gender {
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
            if (value == 0) {
                return Boolean.FALSE;
            } else {
                return Boolean.TRUE;
            }
        }


        // 可选：重写toString方法，使得枚举常量可以直接被转换成字符串
        @Override
        public String toString() {
            return this.description;
        }
    }


}
