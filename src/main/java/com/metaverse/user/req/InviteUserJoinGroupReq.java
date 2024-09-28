package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class InviteUserJoinGroupReq {
    @NotNull(message = "群组ID不能为空")
    @ApiModelProperty(value = "群组ID", required = true)
    private Long groupId;

    @NotNull(message = "被邀请用户ID不能为空")
    @ApiModelProperty(value = "被邀请用户ID", required = true)
    private Long userId;
}
