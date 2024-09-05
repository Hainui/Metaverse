package com.metaverse.user.domain;

import com.metaverse.common.model.IValueObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class MetaverseUserPermission implements IValueObject {

    protected static final Long MODEL_VERSION = 1L;

    /**
     * 用户id
     */
    private Long userId;
    /**
     * 创建时间
     */
    private LocalDateTime createAt;
    /**
     * 创建人
     */
    private Long createBy;
    /**
     * 权限组名
     */
    private String permissionGroupName;
    /**
     * 权限串集合
     */
    private List<String> permissions;

    @Override
    public Long modelVersion() {
        return MODEL_VERSION;
    }
}
