package com.metaverse.card.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class LottreyRecordResp {

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("当天抽奖次数")
    private Integer dailyDrawCount;

    @ApiModelProperty("累计抽奖次数")
    private Long cumulativeDrawCount;

    @ApiModelProperty("上次抽奖时间")
    private LocalDateTime lastDrawTime;

    @ApiModelProperty("已经抽到的卡ID集合")
    private List<CardResp> drawnCardIds;
}
