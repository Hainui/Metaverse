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
 * 好友操作记录表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-24 12:20:37
 */
@Getter
@Setter
@TableName("metaverse_user_friend_operation_log")
@ApiModel(value = "MetaverseUserFriendOperationLogDO对象", description = "好友操作记录表")
@Accessors(chain = true)
public class MetaverseUserFriendOperationLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("执行操作的用户ID")
    private Long userId;

    @ApiModelProperty("目标用户ID（被添加、删除或拉黑的好友ID）")
    private Long targetId;

    @ApiModelProperty("操作类型，1表示添加好友，2表示删除好友，3表示拉黑好友，4表示解除拉黑好友")
    private Integer operationType;

    @ApiModelProperty("操作时间")
    private LocalDateTime operationTime;

    @ApiModelProperty("版本号")
    private Long version;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;
}
