package com.metaverse.user.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class UserGroupResp {
    @ApiModelProperty("群组成员列表")
    private List<UserGroupMemberResp> members;

}
