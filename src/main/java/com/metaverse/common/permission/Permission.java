package com.metaverse.common.permission;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    /**
     * 资源类型集合
     */
    private String[] resourceTypeElements;
    /**
     * 动作
     */
    private String action;
    /**
     * 定位符
     */
    private String locator;
}
