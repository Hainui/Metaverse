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
 * 用户权限关联表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-26 15:50:20
 */
@Getter
@Setter
@TableName("metaverse_user_permission_relationship")
@ApiModel(value = "MetaverseUserPermissionRelationshipDO对象", description = "用户权限关联表")
@Accessors(chain = true)
public class MetaverseUserPermissionRelationshipDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID，关联用户表主键")
    private Long userId;

    @ApiModelProperty("权限ID，关联权限表主键")
    private Long permissionId;

    @ApiModelProperty("授权时间")
    private LocalDateTime impowerAt;

    @ApiModelProperty("授权人ID")
    private Long impowerBy;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;
}
