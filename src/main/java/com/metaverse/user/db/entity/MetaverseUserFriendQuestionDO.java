package com.metaverse.user.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
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
 * 用户好友问题表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-20 10:34:07
 */
@Getter
@Setter
@TableName("metaverse_user_friend_question")
@ApiModel(value = "MetaverseUserFriendQuestionDO对象", description = "用户好友问题表")
@Accessors(chain = true)
public class MetaverseUserFriendQuestionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("问题内容")
    private String question;

    @ApiModelProperty("正确答案")
    private String correctAnswer;

    @ApiModelProperty("是否启用问题，0表示不启用，1表示启用")
    private Boolean enabled;

    @ApiModelProperty("落库时间")
    private Date savedAt;
}