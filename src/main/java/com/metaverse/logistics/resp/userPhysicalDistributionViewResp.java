package com.metaverse.logistics.resp;

import com.metaverse.logistics.dto.DeliveryAddressDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class userPhysicalDistributionViewResp {

    @ApiModelProperty("物流ID")
    private Long id;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("收获地址")
    private DeliveryAddressDto deliveryAddress;

    @ApiModelProperty("快递单号")
    private String trackingNumber;

    @ApiModelProperty("物品名称")
    private String itemName;

    @ApiModelProperty("是否确认收货，0表示未确认，1表示确认")
    private Boolean confirmedReceipt;

    @ApiModelProperty("物品数量，默认为1")
    private Integer itemQuantity;
}
