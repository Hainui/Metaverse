package com.metaverse.user.dto;

import com.metaverse.user.domain.region.dto.MetaverseRegionInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MetaverseUserInfo implements Serializable {

    /**
     * 身份证编号,唯一标识id
     */
    private Long id;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 姓名
     */
    private String name;
    /**
     * 所在区服
     */
    private MetaverseRegionInfo region;
    /**
     * 性别
     */
    private Boolean gender;
    /**
     * 权限
     */
    private MetaverseUserPermissionInfo permission;

}
