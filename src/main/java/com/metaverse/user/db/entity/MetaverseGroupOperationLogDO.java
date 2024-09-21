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
 * 群组操作记录表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@Getter
@Setter
@TableName("metaverse_group_operation_log")
@ApiModel(value = "MetaverseGroupOperationLogDO对象", description = "群组操作记录表")
@Accessors(chain = true)
public class MetaverseGroupOperationLogDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
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
    private Date savedAt;

    @ApiModelProperty("操作时间")
    private Date operationTime;
}
