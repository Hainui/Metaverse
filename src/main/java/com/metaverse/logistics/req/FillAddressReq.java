package com.metaverse.logistics.req;

import com.metaverse.logistics.dto.DeliveryAddressDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class FillAddressReq {

    @ApiModelProperty("物流ID")
    private Long id;

    @ApiModelProperty("用户ID")
    private Long userId;

    @ApiModelProperty("联系电话")
    private String phone;

    @ApiModelProperty("物品名称")
    private String itemName;

    @ApiModelProperty("收获地址（JSON 格式）")
    private DeliveryAddressDto deliveryAddress;
}
