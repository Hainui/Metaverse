package com.metaverse.permission.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MetaverseUserPermissionInfo implements Serializable {

    /**
     * 权限组名
     */
    private String permissionGroupName;
    /**
     * 权限串集合
     */
    private List<String> permissions;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 修改人 没有时为-1
     */
    private Long updateBy;
}
