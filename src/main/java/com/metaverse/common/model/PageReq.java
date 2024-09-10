package com.metaverse.common.model;

import lombok.Data;

@Data
public class PageReq {
    private Integer currentPage = 1;
    private Integer pageSize = 20;

}
