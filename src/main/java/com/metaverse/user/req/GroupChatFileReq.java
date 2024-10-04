package com.metaverse.user.req;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class GroupChatFileReq {
    @ApiModelProperty("群组ID")
    @NotNull(message = "群组ID不能为空")
    private Long groupId;

    @ApiModelProperty("文件ID")
    @NotNull(message = "文件ID不能为空")
    private Long fileId;

    @ApiModelProperty("文件名称")
    @NotNull(message = "文件名称不能为空")
    private String fileName;
}
