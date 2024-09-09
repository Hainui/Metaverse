package com.metaverse.region.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class RegionCreateReq {

    @ApiModelProperty(value = "区服名称", required = true)
    @NotBlank(message = "区服名称不能为空")
    private String name;

    @ApiModelProperty(value = "区服创建请求地址列表", required = true)
    @NotEmpty(message = "区服地址列表不能为空")
    private List<String> serverLocation;
}
