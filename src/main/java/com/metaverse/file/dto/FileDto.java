package com.metaverse.file.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class FileDto {

    @ApiModelProperty("文件ID")
    private Long fileId;

    @ApiModelProperty("文件名称")
    private String fileName;
}
