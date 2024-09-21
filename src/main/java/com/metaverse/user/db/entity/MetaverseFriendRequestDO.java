package com.metaverse.user.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 好友请求表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-21 13:34:07
 */
@Getter
@Setter
@TableName("metaverse_friend_request")
@ApiModel(value = "MetaverseFriendRequestDO对象", description = "好友请求表")
public class MetaverseFriendRequestDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("发起请求的用户ID")
    private Long senderId;

    @ApiModelProperty("接收请求的用户ID")
    private Long receiverId;

    @ApiModelProperty("请求状态，0表示待处理，1表示同意，2表示拒绝")
    private Boolean status;

    @ApiModelProperty("附带的消息")
    private String message;

    @ApiModelProperty("请求创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("请求更新时间")
    private LocalDateTime updatedAt;
}
