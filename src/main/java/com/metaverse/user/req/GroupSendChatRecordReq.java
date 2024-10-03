package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class GroupSendChatRecordReq {

    @ApiModelProperty("群组ID")
    @NotNull(message = "群组ID不能为空")
    private Long groupId;

    @ApiModelProperty("消息内容")
    @Size(max = 2000, message = "消息内容不能超过2000个字符")
    @NotBlank
    private String content;
}
