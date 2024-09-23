package com.metaverse.user.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 元宇宙用户表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-23 17:02:30
 */
@Getter
@Setter
@TableName("metaverse_user")
@ApiModel(value = "MetaverseUserDO对象", description = "元宇宙用户表")
@Accessors(chain = true)
public class MetaverseUserDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID，使用雪花算法生成")
    private Long id;

    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("电子邮件地址，唯一")
    private String email;

    @ApiModelProperty("头像文件id")
    private Long avatarFileId;

    @ApiModelProperty("性别，0表示女，1表示男")
    private Boolean gender;

    @ApiModelProperty("出生时间")
    private LocalDateTime birthTime;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedAt;

    @ApiModelProperty("修改人 ID")
    private Long updateBy;

    @ApiModelProperty("与区域表关联的区域 ID")
    private Long regionId;

    @ApiModelProperty("版本号")
    private Long version;
}
