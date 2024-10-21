package com.metaverse.card.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 抽奖记录表
 * </p>
 *
 * @author Hainui
 * @since 2024-10-21 22:02:55
 */
@Getter
@Setter
@TableName("metaverse_lottery_card_record")
@ApiModel(value = "MetaverseLotteryCardRecordDO对象", description = "抽奖记录表")
@Accessors(chain = true)
public class MetaverseLotteryCardRecordDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("当天抽奖次数")
    private Integer dailyDrawCount;

    @ApiModelProperty("累计抽奖次数")
    private Long cumulativeDrawCount;

    @ApiModelProperty("上次抽奖时间")
    private LocalDateTime lastDrawTime;

    @ApiModelProperty("已经抽到的卡ID集合（JSON格式）")
    private String drawnCardIds;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("更改时间")
    private LocalDateTime updatedAt;

    @ApiModelProperty("版本号")
    private Long version;
}
