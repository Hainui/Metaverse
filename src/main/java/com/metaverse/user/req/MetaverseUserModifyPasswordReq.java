package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MetaverseUserModifyPasswordReq {
    @ApiModelProperty(value = "身份证编号,唯一标识id", required = true)
    @NotNull(message = "身份标识id为空")
    private Long userId;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "密码不能为空")
    private String password;
}
