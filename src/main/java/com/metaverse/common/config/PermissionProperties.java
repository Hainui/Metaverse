package com.metaverse.common.config;

import com.google.common.collect.ImmutableList;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "metaverse.config")
public class PermissionProperties {

    private List<String> systemPermissions;

    public List<String> getSystemPermissions() {
        return ImmutableList.copyOf(systemPermissions);
    }

    public void setSystemPermissions(List<String> systemPermissions) {
        this.systemPermissions = systemPermissions;
    }
}