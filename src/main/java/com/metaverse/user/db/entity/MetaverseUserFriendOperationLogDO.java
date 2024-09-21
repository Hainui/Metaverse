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
 * 好友操作记录表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-21 13:34:07
 */
@Getter
@Setter
@TableName("metaverse_user_friend_operation_log")
@ApiModel(value = "MetaverseUserFriendOperationLogDO对象", description = "好友操作记录表")
public class MetaverseUserFriendOperationLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("执行操作的用户ID")
    private Long userId;

    @ApiModelProperty("目标用户ID（被添加、删除或拉黑的好友ID）")
    private Long targetId;

    @ApiModelProperty("操作类型，1表示添加好友，2表示删除好友，3表示拉黑好友")
    private Boolean operationType;

    @ApiModelProperty("操作时间")
    private LocalDateTime operationTime;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;
}
