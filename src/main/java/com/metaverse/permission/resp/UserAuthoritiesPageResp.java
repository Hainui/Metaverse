package com.metaverse.permission.resp;

import com.metaverse.region.resp.MetaverseRegionResp;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UserAuthoritiesPageResp {
    @ApiModelProperty(value = "用户ID")
    private Long userId;
    @ApiModelProperty(value = "邮箱")
    private String email;
    @ApiModelProperty(value = "用户名")
    private String username;
    @ApiModelProperty(value = "出生时间")
    private LocalDateTime birthTime;
    @ApiModelProperty("性别 true - 男; false - 女")
    private Boolean gender;
    @ApiModelProperty(value = "所在分区")
    private MetaverseRegionResp regionResp;
    @ApiModelProperty(value = "权限集合")
    private List<MetaversePermissionResp> permissionRespList;
    @ApiModelProperty(value = "权限占有最高权限的百分比，计算公式：(用户所有的权限串数量/系统所有权限串数量)*100% 结果向下取整保留两位小数 如：78.34%")
    private String authorizationLevel;
}
