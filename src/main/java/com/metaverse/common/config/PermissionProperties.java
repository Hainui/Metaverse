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

    public static final int UNRESTRICTED_ACCESS_SIZE;

    static {
        UNRESTRICTED_ACCESS_SIZE = countConstants();//todo 计算普通用户能访问的接口 只要不带@Permission注解
    }

    private static int countConstants() {
        return 0;
    }


    public List<String> getSystemPermissions() {
        return ImmutableList.copyOf(systemPermissions);
    }

}