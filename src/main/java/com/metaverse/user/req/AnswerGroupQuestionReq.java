package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AnswerGroupQuestionReq {
    @ApiModelProperty("被请求方群组ID")
    @NotNull(message = "被请求方群组ID不能为空")
    private Long groupId;

    @ApiModelProperty("问题回答文本")
    @NotBlank(message = "问题回答文本不能为空")
    private String questionAnswer;
}
