package com.metaverse.card.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CardTypeReq {
    @ApiModelProperty("卡片名称")
    @NotBlank(message = "卡片名称不能为空")
    private String name;

    @ApiModelProperty("卡片级别（C, S, R, SR, SSR, L）")
    @NotBlank(message = "卡片级别不能为空")
    private String level;
}
