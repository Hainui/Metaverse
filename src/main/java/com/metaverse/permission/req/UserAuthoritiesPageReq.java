package com.metaverse.permission.req;

import com.metaverse.common.model.PageReq;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户分页查询请求参数，均为非必填，值为空表示对该字属性不加限制
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserAuthoritiesPageReq extends PageReq {
    @ApiModelProperty(value = "用户id分页查询参数")
    private Long userId;
    @ApiModelProperty(value = "用户名查询请求参数，右模糊")
    private String username;
    @ApiModelProperty(value = "分区ID查询请求参数")
    private Long regionId;
    @ApiModelProperty(value = "用户邮箱查询请求参数，精确匹配")
    private String email;
    @ApiModelProperty(value = "查询包含该权限的用户")
    private Long permissionId;
    @ApiModelProperty(value = "分页查询在此出生时间之后的用户")
    private LocalDateTime birthTime;
    @ApiModelProperty(value = "分页查询性别限制 false - 女；true - 男 ")
    private Boolean gender;
}
