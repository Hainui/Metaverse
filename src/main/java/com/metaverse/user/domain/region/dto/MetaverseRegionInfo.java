package com.metaverse.user.domain.region.dto;

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
public class MetaverseRegionInfo implements Serializable {
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
}
