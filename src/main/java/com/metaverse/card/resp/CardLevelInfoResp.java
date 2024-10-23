package com.metaverse.card.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CardLevelInfoResp {
    @ApiModelProperty("卡片级别（C, S, R, SR, SSR, L）")
    private String level;

    @ApiModelProperty("卡片信息")
    private List<CardInfoResp> cardInfoList;

    @ApiModelProperty("出货概率")
    private BigDecimal dropRate;
}
