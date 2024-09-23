package com.metaverse.user.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class UserFriendQuestionResp {
    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("问题内容")
    private String question;
}
