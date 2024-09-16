package com.metaverse.common.permission;

import org.springframework.stereotype.Service;

@Service
public class YourService {

    @Permission(
            resourceTypeElements = {"RESOURCE_TYPE_1", "RESOURCE_TYPE_2"},
            action = "ACTION",
            locator = "LOCATOR"
    )
    public void someMethod() {
        // 方法体
        System.out.println("Executing someMethod");
    }
}