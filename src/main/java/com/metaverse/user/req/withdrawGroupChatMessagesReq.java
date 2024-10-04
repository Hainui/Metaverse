package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class withdrawGroupChatMessagesReq {
    @ApiModelProperty("群组Id")
    @NotNull(message = "群组Id不能为空")
    private Long groupId;

    @ApiModelProperty("发送时间")
    @NotNull(message = "发送时间不能为空")
    private LocalDateTime timestamp;
}
