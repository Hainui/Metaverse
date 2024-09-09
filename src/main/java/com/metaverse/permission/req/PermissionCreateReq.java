package com.metaverse.permission.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class PermissionCreateReq {

    @ApiModelProperty(value = "权限名称", required = true)
    @NotBlank(message = "权限名称不能为空")
    private String name;

    @ApiModelProperty(value = "权限串列表", required = true)
    @NotEmpty(message = "权限串列表不能为空")
    private List<String> permissions;
}
