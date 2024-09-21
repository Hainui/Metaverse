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
 * 群组操作记录表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-21 13:34:07
 */
@Getter
@Setter
@TableName("metaverse_group_operation_log")
@ApiModel(value = "MetaverseGroupOperationLogDO对象", description = "群组操作记录表")
public class MetaverseGroupOperationLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("群组ID")
    private Long groupId;

    @ApiModelProperty("执行操作的用户ID")
    private Long operatorId;

    @ApiModelProperty("操作目标的用户ID（例如被踢出群的用户ID）")
    private Long targetId;

    @ApiModelProperty("操作类型，1表示踢出，2表示邀请等，3表示主动申请入群，4表示主动退出群聊")
    private Boolean operationType;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("操作时间")
    private LocalDateTime operationTime;
}
