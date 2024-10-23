package com.metaverse.logistics.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SetTrackingNumberReq {
    @ApiModelProperty("物流ID")
    @NotNull(message = "物流ID不能为空")
    private Long id;
    @ApiModelProperty("快递单号")
    @NotBlank(message = "快递单号不能为空")
    private String trackingNumber;
}
