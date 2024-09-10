package com.metaverse.permission.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 资源类型名称枚举表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-10 22:52:18
 */
@Getter
@Setter
@TableName("metaverse_resource_type_enum")
@ApiModel(value = "MetaverseResourceTypeEnumDO对象", description = "资源类型名称枚举表")
public class MetaverseResourceTypeEnumDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("资源类型名称")
    private String resourceType;
}
