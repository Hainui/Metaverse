package com.metaverse.logistics.db.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
 * 礼品寄送物流表
 * </p>
 *
 * @author Hainui
 * @since 2024-10-23 22:52:21
 */
@Getter
@Setter
@TableName("metaverse_physical_distribution")
@ApiModel(value = "MetaversePhysicalDistributionDO对象", description = "礼品寄送物流表")
@Accessors(chain = true)
public class MetaversePhysicalDistributionDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    @TableId(type = IdType.INPUT)
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("收获地址（JSON 格式）")
    private String deliveryAddress;

    @ApiModelProperty("快递单号")
    private String trackingNumber;

    @ApiModelProperty("物品名称")
    private String itemName;

    @ApiModelProperty("是否确认收货，0表示未确认，1表示确认")
    private Boolean confirmedReceipt;

    @ApiModelProperty("物品数量，默认为1")
    private Integer itemQuantity;

    @ApiModelProperty("落库时间")
    private LocalDateTime savedAt;

    @ApiModelProperty("修改人 ID")
    private Long updateBy;

    @ApiModelProperty("创建人 ID")
    private Long createBy;

    @ApiModelProperty("更改时间")
    private LocalDateTime updatedAt;

    @ApiModelProperty("版本号")
    private Long version;
}
