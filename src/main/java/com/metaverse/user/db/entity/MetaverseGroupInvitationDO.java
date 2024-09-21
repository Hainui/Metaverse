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
 * 邀请入群表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@Getter
@Setter
@TableName("metaverse_group_invitation")
@ApiModel(value = "MetaverseGroupInvitationDO对象", description = "邀请入群表")
@Accessors(chain = true)
public class MetaverseGroupInvitationDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("群组ID")
    private Long groupId;

    @ApiModelProperty("邀请入群的用户ID")
    private Long inviterId;

    @ApiModelProperty("落库时间")
    private Date savedAt;

    @ApiModelProperty("被邀请入群的用户ID")
    private Long inviteeId;

    @ApiModelProperty("邀请入群的信息或理由")
    private String invitationMessage;

    @ApiModelProperty("邀请时间")
    private Date invitationTime;

    @ApiModelProperty("邀请状态，0表示待接受，1表示已接受，2表示拒绝")
    private Boolean status;
}
