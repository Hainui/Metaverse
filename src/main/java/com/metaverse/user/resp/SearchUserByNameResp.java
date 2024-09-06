package com.metaverse.user.resp;

import com.metaverse.user.domain.MetaverseUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class SearchUserByNameResp {
    @ApiModelProperty(value = "身份证编号,唯一标识id")
    private Long userId;
    @ApiModelProperty(value = "姓名")
    private String name;
    @ApiModelProperty(value = "出生时间")
    private LocalDateTime birthTime;
    @ApiModelProperty(value = "性别")
    private MetaverseUser.Gender gender;
}
