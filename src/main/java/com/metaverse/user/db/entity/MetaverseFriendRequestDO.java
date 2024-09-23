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
 * 好友请求表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-23 12:09:41
 */
@Getter
@Setter
@TableName("metaverse_friend_request")
@ApiModel(value = "MetaverseFriendRequestDO对象", description = "好友请求表")
@Accessors(chain = true)
public class MetaverseFriendRequestDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("发起请求的用户ID")
    private Long senderId;

    @ApiModelProperty("接收请求的用户ID")
    private Long receiverId;

    @ApiModelProperty("请求状态，0表示待处理，1表示同意，2表示拒绝")
    private Integer status;

    @ApiModelProperty("附带的消息")
    private String message;

    @ApiModelProperty("请求创建时间")
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
