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
 * 用户群组表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@Getter
@Setter
@TableName("metaverse_user_group")
@ApiModel(value = "MetaverseUserGroupDO对象", description = "用户群组表")
@Accessors(chain = true)
public class MetaverseUserGroupDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("群组名称")
    private String groupName;

    @ApiModelProperty("创建群组的用户ID")
    private Long creatorId;

    @ApiModelProperty("群组描述")
    private String description;

    @ApiModelProperty("落库时间")
    private Date savedAt;

    @ApiModelProperty("群组创建时间")
    private Date createdAt;
}
