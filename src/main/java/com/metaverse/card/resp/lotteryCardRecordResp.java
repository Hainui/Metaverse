package com.metaverse.card.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class lotteryCardRecordResp {

    @ApiModelProperty("卡片ID")
    private Long id;

    @ApiModelProperty("卡片名称")
    private String name;

    @ApiModelProperty("卡片级别（C, S, R, SR, SSR, L）")
    private String level;

    public lotteryCardRecordResp(Long id, String name, String level) {
        this.id = id;
        this.name = name;
        this.level = level;
    }
}
