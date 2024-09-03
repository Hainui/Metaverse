package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class MetaverseUserRegistrationReq {
    @ApiModelProperty(value = "电子邮件地址，唯一", required = true)
    @NotBlank(message = "用户名不能为空")
    private String email;
    @ApiModelProperty(value = "密码", required = true)
    @NotBlank(message = "登录密码不能为空")
    private String password;
    @ApiModelProperty(value = "区服id", required = true)
    @NotNull(message = "分区id未填")
    private Long regionId;
    @ApiModelProperty(value = "姓名", required = true)
    @NotBlank(message = "姓名不能为空")
    private String name;
    @ApiModelProperty(value = "性别，0-女，1-男", required = true)
    @NotNull(message = "性别不能为空")
    private Integer Gender;
    @ApiModelProperty(value = "邮箱验证码", required = true)
    @NotBlank(message = "验证码不能为空")
    private String verifyCode;

}
