package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AnswerUserQuestionReq {
    @ApiModelProperty("被请求方用户ID")
    @NotNull(message = "被请求方用户ID不能为空")
    private Long receiverId;

    @ApiModelProperty("问题答案")
    @NotBlank(message = "回答问题不能为空")
    private String questionAnswer;
}
