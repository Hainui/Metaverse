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
 * 用户权限表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-09
 */
@Getter
@Setter
@TableName("metaverse_permission")
@ApiModel(value = "MetaversePermissionDO对象", description = "用户权限表")
@Accessors(chain = true)
public class MetaversePermissionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("权限ID，使用雪花算法生成")
    private Long id;

    @ApiModelProperty("权限组名")
    private String permissionGroupName;

    @ApiModelProperty("权限组中的权限串")
    private String permissions;

    @ApiModelProperty("权限串创建时间")
    private LocalDateTime createAt;

    @ApiModelProperty("权限串创建人ID")
    private Long createBy;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("最近一次修改时间")
    private LocalDateTime updateAt;

    @ApiModelProperty("权限串修改人 ID")
    private Long updateBy;

    @ApiModelProperty("版本号")
    private Long version;
}
