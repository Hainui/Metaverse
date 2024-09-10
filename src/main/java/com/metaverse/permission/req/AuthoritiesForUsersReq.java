package com.metaverse.permission.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class AuthoritiesForUsersReq {

    @ApiModelProperty(value = "权限ID集合", required = true)
    @NotEmpty(message = "权限不能为空")
    List<Long> permissionIds;
    @ApiModelProperty(value = "用户ID集合", required = true)
    @NotEmpty(message = "用户不能为空")
    List<Long> userIds;
}
