package com.metaverse.user.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserGroupQuestionResp {
    @ApiModelProperty("群组ID")
    private Long groupId;

    @ApiModelProperty("问题内容")
    private String question;
}
