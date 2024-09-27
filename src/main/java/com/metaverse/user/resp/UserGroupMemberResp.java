package com.metaverse.user.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserGroupMemberResp {
    @ApiModelProperty("群组成员的用户ID")
    private Long memberId;

    @ApiModelProperty("成员角色")
    private Integer role;

    @ApiModelProperty("加入群聊时间")
    private LocalDateTime joinedAt;


}
