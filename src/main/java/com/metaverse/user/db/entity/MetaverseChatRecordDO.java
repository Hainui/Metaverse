package com.metaverse.user.db.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 聊天记录表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-23 12:09:41
 */
@Getter
@Setter
@TableName("metaverse_chat_record")
@ApiModel(value = "MetaverseChatRecordDO对象", description = "聊天记录表")
@Accessors(chain = true)
public class MetaverseChatRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("发送消息的用户ID")
    private Long senderId;

    @ApiModelProperty("接收消息的用户ID")
    private Long receiverId;

    @ApiModelProperty("消息类型，1表示文本，2表示图片，3表示语音等")
    private Integer messageType;

    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("多媒体文件的id")
    private Long fileId;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("发送时间")
    private LocalDateTime timestamp;

    @ApiModelProperty("消息是否被撤回，0表示未撤回，1表示已撤回")
    @TableField("is_withdrawn")
    private Boolean withdrawn;

    @ApiModelProperty("消息撤回时间")
    private LocalDateTime withdrawnTime;
}
