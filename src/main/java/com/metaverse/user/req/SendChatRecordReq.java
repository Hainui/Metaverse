package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class SendChatRecordReq {
    @ApiModelProperty("接收消息的用户ID")
    @NotNull(message = "接收消息的用户ID不能为空")
    private Long receiverId;


    @ApiModelProperty("消息内容")
    @Size(max = 65535, message = "消息内容不能超过65535个字符")
    private String content;


}
