package com.metaverse.common.permission;

import com.metaverse.common.Utils.MetaverseContextUtil;
import com.metaverse.common.Utils.PermissionComparator;
import com.metaverse.common.exception.AccessControlPermissionListException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Aspect
@Component
@Order(1) // 可以指定顺序，数值越小优先级越高
public class PermissionAspect {

    @Pointcut("@annotation(com.metaverse.common.permission.Permission)")
    public void needPermission() {
    }

    /**
     * 鉴权
     */
    @Around("needPermission()")
    public Object aroundAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Permission annotation = signature.getMethod().getAnnotation(Permission.class);

        // 访问注解的属性
        String[] resourceTypeElements = annotation.resourceTypeElements();
        String action = annotation.action();
        String locator = annotation.locator();
        List<String> permissions = Optional.of(MetaverseContextUtil.getCurrentUserPermission())
                .orElse(Collections.emptyList())
                .stream()
                .flatMap(permission -> permission.getPermissions().stream())
                .collect(Collectors.toList());
        List<String> errorInfoList = new ArrayList<>();
        for (String resourceTypeElement : resourceTypeElements) {
            StringBuilder sb = new StringBuilder();
            sb.append(resourceTypeElement).append(".").append(action).append(".").append(locator);
            boolean permissionMatched = PermissionComparator.isPermissionMatched(sb.toString(), permissions);
            if (!permissionMatched) {
                if (errorInfoList.isEmpty()) {
                    errorInfoList.add("无权访问以下资源");
                }
                errorInfoList.add(sb.toString());
            }
        }
        if (!errorInfoList.isEmpty()) {
            throw new AccessControlPermissionListException(errorInfoList);
        }
        // 在方法执行前的操作
        System.out.println("Before method execution");
        System.out.println("CustomResourceInterceptor resourceTypeElements: " + String.join(", ", resourceTypeElements));
        System.out.println("CustomResourceInterceptor action: " + action);
        System.out.println("CustomResourceInterceptor locator: " + locator);

        // 继续执行目标方法
        Object result = joinPoint.proceed();

        // 在方法执行后的操作
        System.out.println("After method execution");

        return result;
    }
}