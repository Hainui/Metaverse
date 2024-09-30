package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class GroupQuestionReq {
    @ApiModelProperty("群组ID")
    @NotNull(message = "群组ID不能为空")
    private Long groupId;

    @ApiModelProperty("群组问题")
    @NotNull(message = "群组问题不能为空")
    private String question;

    @ApiModelProperty("问题回答文本")
    @NotBlank(message = "问题回答文本不能为空")
    private String questionAnswer;
}
