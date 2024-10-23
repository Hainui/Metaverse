package com.metaverse.card.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CardInfoResP {
    @ApiModelProperty("卡片ID")
    private Long id;

    @ApiModelProperty("卡片名称")
    private String name;

    @ApiModelProperty("出货概率")
    private BigDecimal dropRate;

}
