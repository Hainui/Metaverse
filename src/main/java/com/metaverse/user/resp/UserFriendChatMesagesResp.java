package com.metaverse.user.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserFriendChatMesagesResp {


    @ApiModelProperty(value = "发送时间")
    private LocalDateTime timestamp;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "消息是否被撤回")
    private boolean isWithdrawn;

    @ApiModelProperty(value = "如果被撤回，撤回时间")
    private LocalDateTime withdrawnTime;

    public UserFriendChatMesagesResp(LocalDateTime timestamp, String content, boolean isWithdrawn, LocalDateTime withdrawnTime) {
        this.timestamp = timestamp;
        this.content = content;
        this.isWithdrawn = isWithdrawn;
        this.withdrawnTime = withdrawnTime;
    }
}
