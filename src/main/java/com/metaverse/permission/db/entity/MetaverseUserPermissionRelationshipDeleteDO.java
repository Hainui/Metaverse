package com.metaverse.permission.db.entity;

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
 * 用户权限关联删除备份表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-09
 */
@Getter
@Setter
@TableName("metaverse_user_permission_relationship_delete")
@ApiModel(value = "MetaverseUserPermissionRelationshipDeleteDO对象", description = "用户权限关联删除备份表")
@Accessors(chain = true)
public class MetaverseUserPermissionRelationshipDeleteDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("关联表主键ID，备份")
    private Long id;

    @ApiModelProperty("用户ID，备份")
    private Long userId;

    @ApiModelProperty("权限ID，备份")
    private Long permissionId;

    @ApiModelProperty("授权时间，备份")
    private LocalDateTime impowerAt;

    @ApiModelProperty("授权人ID，备份")
    private Long impowerBy;

    @ApiModelProperty("删除时间")
    private LocalDateTime deleteAt;

    @ApiModelProperty("删除人ID")
    private Long deleteBy;
}
