package com.metaverse.user.domain.region.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ModifyRegionReq {
    @ApiModelProperty(value = "区服ID", required = true)
    @NotNull(message = "区服id不能为空")
    private Long id;

    @ApiModelProperty(value = "区服名称", required = true)
    @NotBlank(message = "区服名称不能为空")
    private String name;
}
