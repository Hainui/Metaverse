package com.metaverse.permission.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ModifyPermissionReq {
    @ApiModelProperty(value = "权限ID", required = true)
    @NotNull(message = "权限ID不能为空")
    private Long id;

    @ApiModelProperty(value = "权限串列表", required = true)
    @NotEmpty(message = "权限串列表不能为空")
    private List<String> permissions;
}
