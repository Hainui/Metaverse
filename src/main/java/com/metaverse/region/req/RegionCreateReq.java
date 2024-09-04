package com.metaverse.region.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class RegionCreateReq {

    @ApiModelProperty(value = "区服名称", required = true)
    @NotBlank(message = "区服名称不能为空")
    private String name;

    @ApiModelProperty("区服请求地址列表，JSON类型")
    @NotBlank(message = "区服请求地址列表未填")
    private String serverLocation;
}
