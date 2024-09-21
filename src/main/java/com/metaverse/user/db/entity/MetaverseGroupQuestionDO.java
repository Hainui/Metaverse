package com.metaverse.user.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.time.LocalDateTime;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 群组问题表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-21 13:34:07
 */
@Getter
@Setter
@TableName("metaverse_group_question")
@ApiModel(value = "MetaverseGroupQuestionDO对象", description = "群组问题表")
public class MetaverseGroupQuestionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("群组ID")
    private Long groupId;

    @ApiModelProperty("群组问题")
    private String question;

    @ApiModelProperty("正确答案（可选）")
    private String answer;

    @ApiModelProperty("是否启用问题，0表示不启用，1表示启用")
    private Boolean enabled;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;
}
