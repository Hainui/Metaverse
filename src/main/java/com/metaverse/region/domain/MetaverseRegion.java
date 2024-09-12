package com.metaverse.region.domain;

import com.alibaba.fastjson.JSON;
import com.metaverse.common.Utils.BeanManager;
import com.metaverse.common.model.IEntity;
import com.metaverse.region.RegionIdGen;
import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.repository.MetaverseRegionRepository;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@Accessors(chain = true)
public class MetaverseRegion implements IEntity {

    protected static final Long MODEL_VERSION = 1L;

    /**
     * 区服id
     */
    private Long id;
    /**
     * 区服名称
     */
    private String name;
    /**
     * 区服请求地址列表
     */
    private List<String> serverLocation;
    /**
     * 区服创建时间
     */
    private LocalDateTime createAt;
    /**
     * 区服创建人id
     */
    private Long createdBy;

    private Long updatedBy;

    private LocalDateTime updatedAt;

    private Long version;

    public static Long create(String name, List<String> serverLocation, Long currentUserId) {//创建方法
        RegionIdGen idGen = BeanManager.getBean(RegionIdGen.class);
        MetaverseRegionRepository repository = BeanManager.getBean(MetaverseRegionRepository.class);
        MetaverseRegionDO metaverseRegionDO = new MetaverseRegionDO()
                .setId(idGen.nextId())
                .setName(name)
                .setServerLocation(JSON.toJSONString(serverLocation))
                .setCreateBy(currentUserId)
                .setCreateAt(LocalDateTime.now());
        if (!repository.save(metaverseRegionDO)) {
            throw new IllegalArgumentException("分区新建失败");
        }
        return metaverseRegionDO.getId();

    }


    public Boolean modifyRegionName(String newRegionName, Long currentUserId) {
        if (StringUtils.equals(name, newRegionName)) {
            throw new IllegalArgumentException("新区服名不能和旧区服名相同");
        }
        MetaverseRegionRepository repository = BeanManager.getBean(MetaverseRegionRepository.class);
        if (repository.existByName(newRegionName)) {
            throw new IllegalArgumentException("该区服名已存在");
        }
        Long newVersion = changeVersion();
        return repository.updateRegionName(pkVal(), newRegionName, currentUserId, newVersion);
    }

    public static MetaverseRegion writeLoadAndAssertNotExist(Long Id) {
        MetaverseRegionRepository repository = BeanManager.getBean(MetaverseRegionRepository.class);
        MetaverseRegion region = repository.findByIdWithWriteLock(Id);
        if (Objects.isNull(region)) {
            throw new IllegalArgumentException("未找到该用户信息");
        }
        return region;
    }

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

    public Boolean modifyRegionLocationList(List<String> newServerLocation, Long currentUserId) {
        if (Objects.equals(newServerLocation, serverLocation)) {
            throw new IllegalArgumentException("新区服地址和旧区服地址完全相同，无需修改");
        }
        MetaverseRegionRepository repository = BeanManager.getBean(MetaverseRegionRepository.class);
        Long newVersion = changeVersion();
        return repository.modifyRegionLocationList(newServerLocation, pkVal(), currentUserId, newVersion);
    }
}
