package com.metaverse.user.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class UserFriendChatMessagesResp {
    @ApiModelProperty(value = "发送信息的用户ID")
    private Long senderId;

    @ApiModelProperty("接收消息的用户ID")
    private Long receiverId;

    @ApiModelProperty(value = "发送时间")
    private LocalDateTime timestamp;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "文件id")
    private Long fileId;

    @ApiModelProperty(value = "消息是否被撤回")
    private boolean isWithdrawn;

    @ApiModelProperty(value = "如果被撤回，撤回时间")
    private LocalDateTime withdrawnTime;

    @ApiModelProperty("消息类型，0表示文本或者图片，1表示语音等")
    private Integer messageType;

    public UserFriendChatMessagesResp(Long senderId, Long receiverId, LocalDateTime timestamp, String content, Long fileId, boolean isWithdrawn, LocalDateTime withdrawnTime, Integer messageType) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.timestamp = timestamp;
        this.content = content;
        this.fileId = fileId;
        this.isWithdrawn = isWithdrawn;
        this.withdrawnTime = withdrawnTime;
        this.messageType = messageType;
    }
}
