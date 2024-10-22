package com.metaverse.card.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CardResp {

    @ApiModelProperty("卡片ID")
    private Long id;

    @ApiModelProperty("卡片名称")
    private String name;

    @ApiModelProperty("卡片级别（C, S, R, SR, SSR, L）")
    private String level;
}
