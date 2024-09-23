package com.metaverse.file.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SignedEncryptedUrlReq {
    @NotBlank(message = "路由地址不能为空")
    @ApiModelProperty(value = "加签加密后的路由地址", required = true)
    String signedEncryptedUrl;
}
