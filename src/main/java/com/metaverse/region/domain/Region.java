package com.metaverse.region.domain;

import java.time.LocalDateTime;
import java.util.List;

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
    private Long createBy;
}
