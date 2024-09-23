package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class AddFriendReq {

    @ApiModelProperty("接收请求的用户ID")
    @NotNull(message = "接收请求的用户ID不能为空")
    private Long receiverId;

    @ApiModelProperty("附带的消息")
    private String message;
}
