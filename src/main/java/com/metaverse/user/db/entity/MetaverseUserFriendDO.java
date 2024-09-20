package com.metaverse.user.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 用户好友关系表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@Getter
@Setter
@TableName("metaverse_user_friend")
@ApiModel(value = "MetaverseUserFriendDO对象", description = "用户好友关系表")
@Accessors(chain = true)
public class MetaverseUserFriendDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("好友ID")
    private Long friendId;

    @ApiModelProperty("关系类型，1表示好友，2表示黑名单")
    private Boolean relation;

    @ApiModelProperty("状态，1表示正常，2表示删除")
    private Boolean status;

    @ApiModelProperty("亲密度等级，默认为0%")
    private BigDecimal intimacyLevel;

    @ApiModelProperty("创建时间")
    private Date createdAt;

    @ApiModelProperty("落库时间")
    private Date savedAt;
}
