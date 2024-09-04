package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ModifyUserNameReq {

    @ApiModelProperty(value = "身份证编号,唯一标识id", required = true)
    @NotNull(message = "身份标识id为空")
    private Long id;

    @ApiModelProperty(value = "姓名", required = true)
    @NotBlank(message = "姓名不能为空")
    private String name;
}
