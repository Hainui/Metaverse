package com.metaverse.user.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户群组成员表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@Getter
@Setter
@TableName("metaverse_user_group_member")
@ApiModel(value = "MetaverseUserGroupMemberDO对象", description = "用户群组成员表")
@Accessors(chain = true)
public class MetaverseUserGroupMemberDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("群组ID")
    private Long groupId;

    @ApiModelProperty("群组成员的用户ID")
    private Long memberId;

    @ApiModelProperty("成员角色，0表示普通成员，1表示管理员，2表示群主")
    private Boolean role;

    @ApiModelProperty("加入群组的时间")
    private Date joinedAt;

    @ApiModelProperty("落库时间")
    private Date savedAt;
}