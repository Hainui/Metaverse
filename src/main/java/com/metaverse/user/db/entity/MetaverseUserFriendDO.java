package com.metaverse.user.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 用户好友关系表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-23 12:09:41
 */
@Getter
@Setter
@TableName("metaverse_user_friend")
@ApiModel(value = "MetaverseUserFriendDO对象", description = "用户好友关系表")
@Accessors(chain = true)
public class MetaverseUserFriendDO implements Serializable {

    private static final long serialVersionUID = 1L;

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
    private LocalDateTime createdAt;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedAt;

    @ApiModelProperty("修改人 ID")
    private Long updateBy;

    @ApiModelProperty("版本号")
    private Long version;
}
