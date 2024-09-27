package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class CreateUserGroupReq {

    @ApiModelProperty(value = "群组名称", required = true)
    @NotBlank(message = "群组名称不能为空")
    private String groupName;

    @ApiModelProperty(value = "群组描述", required = true)
    @NotBlank(message = "群组描述不能为空")
    private String description;

    @ApiModelProperty("群组成员的用户ID")
    @Size(min = 2, message = "群成员数量至少为三人")
    private List<Long> memberIds;
}
