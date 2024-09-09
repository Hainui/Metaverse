package com.metaverse.permission.resp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class MetaversePermissionResp {

    /**
     * 权限ID
     */
    private Long id;
    /**
     * 权限组名
     */
    private String permissionGroupName;
}
