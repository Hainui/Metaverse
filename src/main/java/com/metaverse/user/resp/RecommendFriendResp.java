package com.metaverse.user.resp;

import com.metaverse.user.dto.MetaverseUserAbstractInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class RecommendFriendResp {

    @ApiModelProperty(value = "用户简洁信息")
    private MetaverseUserAbstractInfo userAbstractInfo;

    @ApiModelProperty(value = "共同好友个数")
    private Integer mutualFriendCount;
}
