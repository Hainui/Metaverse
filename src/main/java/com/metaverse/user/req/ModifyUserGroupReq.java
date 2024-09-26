package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ModifyUserGroupReq {

    @ApiModelProperty(value = "群组ID", required = true)
    @NotNull(message = "群组ID不能为空")
    private Long userGroupId;

    @ApiModelProperty(value = "群组名称", required = true)
    @NotBlank(message = "群组名称不能为空")
    private String groupName;

    @ApiModelProperty(value = "群组描述", required = true)
    @NotBlank(message = "群组描述不能为空")
    private String description;

    @ApiModelProperty("群组成员的用户ID")
    private List<Long> memberIds;
}
