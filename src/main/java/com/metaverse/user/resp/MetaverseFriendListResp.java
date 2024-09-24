package com.metaverse.user.resp;

import com.metaverse.user.domain.MetaverseUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class MetaverseFriendListResp {


    @ApiModelProperty(value = "好友ID")
    private Long friendId;

    @ApiModelProperty(value = "好友姓名")
    private String friendName;

    @ApiModelProperty(value = "好友头像文件ID")
    private Long friendAvatarFileId;

    @ApiModelProperty(value = "性别")
    private MetaverseUser.Gender gender;

    @ApiModelProperty(value = "关系类型，1表示好友，2表示黑名单")
    private Integer relation;

    @ApiModelProperty(value = "亲密度等级")
    private BigDecimal intimacyLevel;

    @ApiModelProperty(value = "好友状态，1表示正常，2表示删除")
    private Integer status;


}