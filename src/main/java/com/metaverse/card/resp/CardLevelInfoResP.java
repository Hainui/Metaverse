package com.metaverse.card.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardLevelInfoResP {
    @ApiModelProperty("卡片级别（C, S, R, SR, SSR, L）")
    private String level;

    @ApiModelProperty("卡片信息")
    private List<CardInfoResP> cardInfoList;
}
