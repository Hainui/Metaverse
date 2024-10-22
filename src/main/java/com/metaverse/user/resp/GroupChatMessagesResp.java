package com.metaverse.user.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class GroupChatMessagesResp {
    @ApiModelProperty(value = "发送信息的用户ID")
    private Long senderId;
    //todo 多一个发送人名称

    @ApiModelProperty(value = "发送时间")
    private LocalDateTime timestamp;

    @ApiModelProperty("消息类型，0表示文本或者图片，1表示语音等")
    private Integer messageType;

    @ApiModelProperty(value = "消息内容")
    private String content;

    @ApiModelProperty(value = "文件id")
    private Long fileId;

    @ApiModelProperty(value = "消息是否被撤回")
    private boolean isWithdrawn;

    @ApiModelProperty(value = "如果被撤回，撤回时间")
    private LocalDateTime withdrawnTime;

    public GroupChatMessagesResp(Long senderId, LocalDateTime timestamp, Integer messageType, String content, Long fileId, Boolean withdrawn, LocalDateTime withdrawnTime) {
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.messageType = messageType;
        this.content = content;
        this.fileId = fileId;
        this.isWithdrawn = withdrawn;
        this.withdrawnTime = withdrawnTime;
    }
}
