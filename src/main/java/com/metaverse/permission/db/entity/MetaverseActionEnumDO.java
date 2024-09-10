package com.metaverse.permission.db.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * <p>
 * 动作类型名称枚举表
 * </p>
 *
 * @author Hainui
 * @since 2024-09-10 22:52:18
 */
@Getter
@Setter
@TableName("metaverse_action_enum")
@ApiModel(value = "MetaverseActionEnumDO对象", description = "动作类型名称枚举表")
public class MetaverseActionEnumDO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("动作类型名称")
    private String action;
}
