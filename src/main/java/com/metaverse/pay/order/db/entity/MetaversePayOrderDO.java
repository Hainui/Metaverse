package com.metaverse.pay.order.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * 支付单表
 * </p>
 *
 * @author Hainui
 * @since 2024-10-27 06:39:55
 */
@Getter
@Setter
@TableName("metaverse_pay_order")
@ApiModel(value = "MetaversePayOrderDO对象", description = "支付单表")
@Accessors(chain = true)
public class MetaversePayOrderDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键支付单ID")
    @TableId(type = IdType.INPUT)
    private Long id;

    @ApiModelProperty("物品名称")
    private String body;

    @ApiModelProperty("物品数量")
    private Integer quantity;

    @ApiModelProperty("金额")
    private BigDecimal money;

    @ApiModelProperty("支付单状态，0未支付，1支付成功")
    private Boolean status;

    @ApiModelProperty("微信支付单号")
    private String weChatPayNo;

    @ApiModelProperty("支付宝支付单号")
    private String alipayPayNo;

    @ApiModelProperty("订单创建时间")
    private LocalDateTime createAt;

    @ApiModelProperty("支付时间")
    private LocalDateTime payAt;

    @ApiModelProperty("支付单创建人ID")
    private Long createBy;

    @ApiModelProperty("版本号")
    private Long version;
}
