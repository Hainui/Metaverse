package com.metaverse.region.resp;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegionListResp {

    @ApiModelProperty(value = "区服id", required = true)
    private Long id;

    @ApiModelProperty(value = "区服名称", required = true)
    private String name;
}
