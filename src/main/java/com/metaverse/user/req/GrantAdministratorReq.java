package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class GrantAdministratorReq {
    @NotNull(message = "群组ID不能为空")
    @ApiModelProperty(value = "群组ID", required = true)
    private Long groupId;

    @NotNull(message = "群成员用户ID不能为空")
    @ApiModelProperty(value = "群成员用户", required = true)
    private Long memberId;
}
