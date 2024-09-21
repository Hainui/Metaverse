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
 * 用户群组表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-21 13:34:07
 */
@Getter
@Setter
@TableName("metaverse_user_group")
@ApiModel(value = "MetaverseUserGroupDO对象", description = "用户群组表")
public class MetaverseUserGroupDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("群组名称")
    private String groupName;

    @ApiModelProperty("创建群组的用户ID")
    private Long creatorId;

    @ApiModelProperty("群组描述")
    private String description;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("群组创建时间")
    private LocalDateTime createdAt;
}
