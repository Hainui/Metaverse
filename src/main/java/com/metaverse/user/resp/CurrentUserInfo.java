package com.metaverse.user.resp;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CurrentUserInfo {
    @ApiModelProperty(value = "身份证编号,唯一标识id")
    private Long userId;
    @ApiModelProperty("与区域表关联的区域 ID")
    private Long regionId;
}
