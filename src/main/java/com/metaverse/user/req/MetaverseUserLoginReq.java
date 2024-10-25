package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MetaverseUserLoginReq {

    @ApiModelProperty(value = "电子邮件地址，唯一", required = true)
    @NotBlank(message = "电子邮件不能为空")
    @Email(message = "电子邮件格式不正确")
    private String email;

    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "登录密码不能为空")
    private String password;

    @ApiModelProperty(value = "区服id", required = true)
    @NotNull(message = "区服id未填")
    private Long regionId;

    @ApiModelProperty(value = "微信登录授权码")
    private String wechatCode;
}
