package com.metaverse.card.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class CardTypeReq {
    @ApiModelProperty("卡片名称")
    @NotNull(message = "卡片名称不能为空")
    private String name;

    @ApiModelProperty("卡片级别（C, S, R, SR, SSR, L）")
    @NotNull(message = "卡片级别不能为空")
    private String level;

    @ApiModelProperty("卡片概率")
    @NotNull(message = "卡片概率不能为空")
    private BigDecimal dropRate;
}
