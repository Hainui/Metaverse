package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class SendChatAudioReq {
    @ApiModelProperty("接收消息的用户ID")
    @NotNull(message = "接收消息的用户ID不能为空")
    private Long receiverId;
    
    @ApiModelProperty("音频文件ID")
    @NotNull(message = "音频文件ID不能为空")
    private Long fileId;
}
