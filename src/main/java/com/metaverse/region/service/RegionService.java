package com.metaverse.region.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class RegionService {


    public Long create() {
        return 111L;
    }


    public boolean change() {
        return Boolean.TRUE;
    }
}
