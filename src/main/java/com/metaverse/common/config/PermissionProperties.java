package com.metaverse.common.config;

import com.google.common.collect.ImmutableList;
import com.metaverse.common.permission.Permission;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Setter
@Component
@ConfigurationProperties(prefix = "metaverse.config")
public class PermissionProperties {

    private List<String> systemPermissions;

    public static final int UNRESTRICTED_ACCESS_SIZE;

    static {
        try {
            UNRESTRICTED_ACCESS_SIZE = countUnrestrictedAccessMethods();
        } catch (Exception e) {
            throw new RuntimeException("初始化(UNRESTRICTED_ACCESS_SIZE)失败", e);
        }
    }

    private static int countUnrestrictedAccessMethods() {
        String basePackage = "com.metaverse";
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(false);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Controller.class));
        Set<BeanDefinition> controllerBeanDefinitions = scanner.findCandidateComponents(basePackage);
        int count = 0;

        for (BeanDefinition controllerBeanDefinition : controllerBeanDefinitions) {
            try {
                String controllerClassName = controllerBeanDefinition.getBeanClassName();
                Class<?> controllerClass = Class.forName(controllerClassName);
                Method[] methods = controllerClass.getDeclaredMethods();

                for (Method method : methods) {
                    if (!method.isAnnotationPresent(Permission.class)) {
                        count++;
                    }
                }
            } catch (ClassNotFoundException | NoClassDefFoundError e) {
                log.error("加载控制器类时出错", e);
            }
        }

        return count;
    }


    public List<String> getSystemPermissions() {
        return ImmutableList.copyOf(Optional.of(systemPermissions).orElse(ImmutableList.of()));
    }

}