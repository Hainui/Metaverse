package com.metaverse.region.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class ModifyRegionServerLocationReq {
    @ApiModelProperty(value = "区服ID", required = true)
    @NotNull(message = "区服id不能为空")
    private Long id;

    @ApiModelProperty("区服请求地址列表")
    @NotEmpty(message = "区服地址列表不能为空")
    private List<String> serverLocation;
}
