package com.metaverse.user.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.util.Date;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * <p>
 * 聊天记录表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@Getter
@Setter
@TableName("metaverse_chat_record")
@ApiModel(value = "MetaverseChatRecordDO对象", description = "聊天记录表")
@Accessors(chain = true)
public class MetaverseChatRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("发送消息的用户ID")
    private Long senderId;

    @ApiModelProperty("接收消息的用户ID")
    private Long receiverId;

    @ApiModelProperty("消息类型，1表示文本，2表示图片，3表示语音等")
    private Boolean messageType;

    @ApiModelProperty("消息内容")
    private String content;

    @ApiModelProperty("多媒体文件的路径或URL")
    private String filePath;

    @ApiModelProperty("落库时间")
    private Date savedAt;

    @ApiModelProperty("发送时间")
    private Date timestamp;

    @ApiModelProperty("消息是否被撤回，0表示未撤回，1表示已撤回")
    @TableField("is_withdrawn")
    private Boolean withdrawn;
}
