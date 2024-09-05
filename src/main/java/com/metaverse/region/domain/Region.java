package com.metaverse.region.domain;

import com.alibaba.fastjson.JSON;
import com.metaverse.common.Utils.BCryptUtil;
import com.metaverse.common.Utils.BeanManager;
import com.metaverse.region.RegionIdGen;
import com.metaverse.region.db.entity.MetaverseRegionDO;
import com.metaverse.region.repository.MetaverseRegionRepository;
import com.metaverse.user.UserIdGen;
import com.metaverse.user.db.entity.MetaverseUserDO;
import com.metaverse.user.domain.MetaverseUser;
import com.metaverse.user.repository.MetaverseUserRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Region {
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

    private Date createdAt;

    private Long updatedBy;

    private Date updatedAt;

    private Long version;

    public static Long create(String name,List<String> serverLocation,Long currentUserId){
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
}
