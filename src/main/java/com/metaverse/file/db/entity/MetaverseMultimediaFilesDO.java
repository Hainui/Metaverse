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
 * @since 2024-09-23 09:00:23
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

    private String url;

    @ApiModelProperty("上传人id")
    private Long uploaderId;

    @ApiModelProperty("上传时间")
    private LocalDateTime uploadTime;
}
