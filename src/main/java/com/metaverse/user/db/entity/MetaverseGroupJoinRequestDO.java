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
 * 入群申请表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@Getter
@Setter
@TableName("metaverse_group_join_request")
@ApiModel(value = "MetaverseGroupJoinRequestDO对象", description = "入群申请表")
@Accessors(chain = true)
public class MetaverseGroupJoinRequestDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("群组ID")
    private Long groupId;

    @ApiModelProperty("申请入群的用户ID")
    private Long requesterId;

    @ApiModelProperty("申请入群的信息或理由")
    private String requestMessage;

    @ApiModelProperty("申请时间")
    private Date requestTime;

    @ApiModelProperty("落库时间")
    private Date savedAt;

    @ApiModelProperty("申请状态，0表示待审核，1表示已批准，2表示拒绝")
    private Boolean status;
}