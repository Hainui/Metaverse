package com.metaverse.login.domain;

import com.metaverse.login.common.Gender;

import java.time.LocalDateTime;

public class MetaverseUser {
    /**
     * 邮箱
     */
    private String email;
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


}
