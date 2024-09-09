package com.metaverse.permission.domain;

import com.metaverse.common.model.IEntity;
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
public class MetaversePermission implements IEntity {

    protected static final Long MODEL_VERSION = 1L;
    /**
     * 权限ID
     */
    private Long id;
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
     * 创建时间
     */
    private LocalDateTime createAt;
    /**
     * 最近一次修改时间
     */
    private LocalDateTime updatedAt;
    /**
     * 修改人
     */
    private Long updatedBy;
    /**
     * 版本号，每次变更+1
     */
    private Long version;

    @Override
    public Long pkVal() {
        return id;
    }

    @Override
    public Long modelVersion() {
        return MODEL_VERSION;
    }

    @Override
    public Long changeVersion() {
        return ++version;
    }
}
