package com.metaverse.region.domain;

import com.alibaba.fastjson.JSON;
import com.metaverse.common.Utils.BeanManager;
import com.metaverse.common.model.IAggregateRoot;
import com.metaverse.region.RegionIdGen;
import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.repository.MetaverseRegionRepository;
import com.metaverse.region.req.ModifyRegionReq;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)//用于链式方法
public class MetaverseRegion implements IAggregateRoot<MetaverseRegion> {

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
        repository.save(metaverseRegionDO);
        return metaverseRegionDO.getId();

    }


    public Boolean modifyRegionName(ModifyRegionReq req, Long currentUserId) {
        if (StringUtils.equals(name, req.getName())) {
            throw new IllegalArgumentException("修改前名字不能和原来名字相同");
        }
        MetaverseRegionRepository repository = BeanManager.getBean(MetaverseRegionRepository.class);
        if (repository.existByName(req.getName())) {
            throw new IllegalArgumentException("名字已经存在");
        }
        Long newVersion = version + 1;
        return repository.updateRegionName(req.getId(), req.getName(), currentUserId, newVersion);
    }

    public static MetaverseRegion loadAndAssertNotExist(Long Id) {
        MetaverseRegionRepository repository = BeanManager.getBean(MetaverseRegionRepository.class);
        MetaverseRegion region = repository.findByIdWithLock(Id);
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
}
