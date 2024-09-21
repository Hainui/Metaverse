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
 * 邀请入群表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-21 13:34:07
 */
@Getter
@Setter
@TableName("metaverse_group_invitation")
@ApiModel(value = "MetaverseGroupInvitationDO对象", description = "邀请入群表")
public class MetaverseGroupInvitationDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("群组ID")
    private Long groupId;

    @ApiModelProperty("邀请入群的用户ID")
    private Long inviterId;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("被邀请入群的用户ID")
    private Long inviteeId;

    @ApiModelProperty("邀请入群的信息或理由")
    private String invitationMessage;

    @ApiModelProperty("邀请时间")
    private LocalDateTime invitationTime;

    @ApiModelProperty("邀请状态，0表示待接受，1表示已接受，2表示拒绝")
    private Boolean status;
}
