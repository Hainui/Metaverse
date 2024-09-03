package com.metaverse.user.domain;

import com.metaverse.common.Utils.BCryptUtil;
import com.metaverse.common.Utils.BeanManager;
import com.metaverse.common.Utils.SnowflakeIdWorker;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.repository.MetaverseUserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
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


    public static boolean registration(String name, String email, String password, Long regionId, Gender gender) {
        SnowflakeIdWorker idGen = BeanManager.getBean(SnowflakeIdWorker.class);
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
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
                .setBirthTime(LocalDateTime.now());
        return repository.save(entity);
    }


    public static boolean login(String email, String password, Long regionId) {
        MetaverseUserRepository repository = BeanManager.getBean(MetaverseUserRepository.class);
        return repository.login(email, password,regionId);
    }



    @Getter
    public enum Gender {
        // 定义枚举常量 FEMALE 和 MALE
        FEMALE(0, "女"),
        MALE(1, "男");

        // 获取性别的整数值
        // 成员变量用于存储性别的整数值和描述
        private int value;
        // 获取性别的描述
        private String description;

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
