package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddGroupReq {
    @ApiModelProperty("接收请求的群组ID")
    @NotNull(message = "接收请求的群组ID不能为空")
    private Long receiverGroupId;

    @ApiModelProperty("附带的消息")
    private String message;
}
