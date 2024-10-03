package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SendChatFileReq {
    @ApiModelProperty("接收消息的用户ID")
    @NotNull(message = "接收消息的用户ID不能为空")
    private Long receiverId;

    @ApiModelProperty("文件ID")
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @ApiModelProperty("文件名称")
    @NotBlank(message = "文件名称不能为空")
    private String fileName;
}
