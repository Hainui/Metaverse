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
 * 群组操作记录表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-23 12:09:41
 */
@Getter
@Setter
@TableName("metaverse_group_operation_log")
@ApiModel(value = "MetaverseGroupOperationLogDO对象", description = "群组操作记录表")
@Accessors(chain = true)
public class MetaverseGroupOperationLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("群组ID")
    private Long groupId;

    @ApiModelProperty("执行操作的用户ID")
    private Long operatorId;

    @ApiModelProperty("操作目标的用户ID（例如被踢出群的用户ID）")
    private Long targetId;

    @ApiModelProperty("操作类型，1表示踢出（被动退群），2表示邀请用户同意入群等（被动入群），3表示主动申请入群-管理员同意或者回答对问题（主动入群），4表示主动退出群聊（主动退群）")
    private Integer operationType;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("操作时间")
    private LocalDateTime operationTime;
}
