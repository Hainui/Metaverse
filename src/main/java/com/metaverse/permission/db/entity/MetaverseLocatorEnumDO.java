package com.metaverse.permission.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 定位符类型名称枚举表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-10 22:52:18
 */
@Getter
@Setter
@TableName("metaverse_locator_enum")
@ApiModel(value = "MetaverseLocatorEnumDO对象", description = "定位符类型名称枚举表")
public class MetaverseLocatorEnumDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("定位符类型名称")
    private String locator;
}