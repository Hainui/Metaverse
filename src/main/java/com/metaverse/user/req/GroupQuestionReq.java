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
    @NotBlank(message = "群组问题不能为空")
    private String question;

    @ApiModelProperty("问题答案")
    @NotBlank(message = "问题答案不能为空")
    private String questionAnswer;
}
