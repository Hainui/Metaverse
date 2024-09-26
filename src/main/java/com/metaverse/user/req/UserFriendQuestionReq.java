package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserFriendQuestionReq {
    @ApiModelProperty("问题内容")
    @NotBlank(message = "问题内容不能为空")
    private String question;

    @ApiModelProperty("正确答案")
    @NotBlank(message = "问题答案不能为空")
    private String correctAnswer;
}
