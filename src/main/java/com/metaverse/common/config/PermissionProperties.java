package com.metaverse.common.config;

import com.google.common.collect.ImmutableList;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Component
@ConfigurationProperties(prefix = "metaverse.config")
public class PermissionProperties {

    private List<String> systemPermissions;

    public List<String> getSystemPermissions() {
        return ImmutableList.copyOf(systemPermissions);
    }

}