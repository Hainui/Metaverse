package com.metaverse.permission.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ModifyPermissionNameReq {
    @ApiModelProperty(value = "权限ID", required = true)
    @NotNull(message = "权限ID不能为空")
    private Long id;

    @ApiModelProperty(value = "权限名称", required = true)
    @NotBlank(message = "权限名称不能为空")
    private String name;
}
