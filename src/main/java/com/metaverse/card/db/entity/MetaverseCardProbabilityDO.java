package com.metaverse.card.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * <p>
 * 抽卡概率表
 * </p>
 *
 * @author Hainui
 * @since 2024-10-23 21:49:52
 */
@Getter
@Setter
@TableName("metaverse_card_probability")
@ApiModel(value = "MetaverseCardProbabilityDO对象", description = "抽卡概率表")
@Accessors(chain = true)
public class MetaverseCardProbabilityDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("卡片ID")
    private Long id;

    @ApiModelProperty("卡片名称")
    private String name;

    @ApiModelProperty("卡片级别（C, S, R, SR, SSR, L）")
    private String level;

    @ApiModelProperty("出货概率")
    private BigDecimal dropRate;

    @ApiModelProperty("创建时间")
    private LocalDateTime createdAt;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("更新时间")
    private LocalDateTime updatedAt;

    @ApiModelProperty("修改人 ID")
    private Long updateBy;

    @ApiModelProperty("创建人 ID")
    private Long createBy;

    @ApiModelProperty("版本号")
    private Long version;
}
