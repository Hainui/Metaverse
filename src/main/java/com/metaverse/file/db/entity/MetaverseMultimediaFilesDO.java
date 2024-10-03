package com.metaverse.file.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 多媒体文件表
 * </p>
 *
 * @author Hainui
 * @since 2024-10-03 22:48:07
 */
@Getter
@Setter
@TableName("metaverse_multimedia_files")
@ApiModel(value = "MetaverseMultimediaFilesDO对象", description = "多媒体文件表")
@Accessors(chain = true)
public class MetaverseMultimediaFilesDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键ID")
    private Long id;

    @ApiModelProperty("文件名称")
    private String name;

    private String url;

    @ApiModelProperty("上传人id")
    private Long uploaderId;

    @ApiModelProperty("上传时间")
    private LocalDateTime uploadTime;
}
