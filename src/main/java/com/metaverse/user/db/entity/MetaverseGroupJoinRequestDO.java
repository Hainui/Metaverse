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
 * 入群申请表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-21 13:34:07
 */
@Getter
@Setter
@TableName("metaverse_group_join_request")
@ApiModel(value = "MetaverseGroupJoinRequestDO对象", description = "入群申请表")
public class MetaverseGroupJoinRequestDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("群组ID")
    private Long groupId;

    @ApiModelProperty("申请入群的用户ID")
    private Long requesterId;

    @ApiModelProperty("申请入群的信息或理由")
    private String requestMessage;

    @ApiModelProperty("申请时间")
    private LocalDateTime requestTime;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("申请状态，0表示待审核，1表示已批准，2表示拒绝")
    private Boolean status;
}
