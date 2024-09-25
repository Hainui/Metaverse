package com.metaverse.user.resp;

import com.metaverse.user.domain.MetaverseUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@Accessors(chain = true)
public class MetaverseUserAbstractInfo {

    @ApiModelProperty(value = "用户ID")
    private Long userId;

    @ApiModelProperty(value = "好友姓名")
    private String name;

    @ApiModelProperty(value = "用户头像文件ID")
    private Long avatarImageId;

    @ApiModelProperty(value = "性别")
    private MetaverseUser.Gender gender;

    @ApiModelProperty(value = "亲密度等级")
    private BigDecimal intimacyLevel;


}