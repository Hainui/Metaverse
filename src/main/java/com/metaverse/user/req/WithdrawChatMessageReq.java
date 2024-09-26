package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class WithdrawChatMessageReq {

    @ApiModelProperty("接收信息的用户Id")
    @NotNull(message = "接收信息的用户Id不能为空")
    private Long receiverId;

    @ApiModelProperty("发送时间")
    @NotNull(message = "发送时间不能为空")
    private LocalDateTime timestamp;
}
