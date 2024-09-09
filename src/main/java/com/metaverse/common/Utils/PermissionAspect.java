//package com.metaverse.common.Utils;
//
//
//import io.bigplayers.tangerine.security.spring.permission.annotation.Permission;
//import io.bigplayers.tangerine.security.spring.permission.constant.enums.MatchValuePolicy;
//import io.bigplayers.tangerine.security.spring.permission.util.AspectExpressUtil;
//import io.bigplayers.tangerine.security.spring.permission.util.PermissionBuilder;
//import lombok.Data;
//import lombok.experimental.Accessors;
//import lombok.extern.slf4j.Slf4j;
//import org.aspectj.lang.ProceedingJoinPoint;
//import org.aspectj.lang.annotation.Around;
//import org.aspectj.lang.annotation.Aspect;
//import org.aspectj.lang.annotation.Pointcut;
//import org.aspectj.lang.reflect.MethodSignature;
//import org.springframework.expression.EvaluationContext;
//import org.springframework.util.StringUtils;
//
//import java.lang.reflect.Method;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//import java.util.concurrent.ConcurrentHashMap;
//
///**
// * 鉴权切面
// */
//@Aspect
//@Slf4j
//public class PermissionAspect {
//
//    private final PermissionProperties properties;
//
//    /**
//     * 存储切入点方法数据
//     */
//    private static final ConcurrentHashMap<String, MetaData> METADATA_MAP = new ConcurrentHashMap<>();
//
//    public PermissionAspect(PermissionProperties properties) {
//        this.properties = properties;
//    }
//
//    @Pointcut("@annotation(io.bigplayers.tangerine.security.spring.permission.annotation.Permission)||@annotation(io.bigplayers.tangerine.security.spring.permission.annotation.Permissions)")
//    public void needPermission() {
//
//    }
//
//    /**
//     * 鉴权
//     */
//    @Around(value = "needPermission()")
//    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
//        if (!properties.isEnable()) {
//            return joinPoint.proceed();
//        }
//        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
//        MetaData metaData = getMetaData(signature, joinPoint);
//        Method method = metaData.method;
//        Permission[] permissions = metaData.permissions;
//        int length = permissions.length;
//        List<String> permissionList = new ArrayList<>(length);
//        EvaluationContext context = null;
//        for (Permission permission : permissions) {
//            String permissionStr = permission.value();
//            if (!StringUtils.hasText(permissionStr)) {
//                String resourceType = permission.resourceType();
//                if (!StringUtils.hasText(resourceType)) {
//                    resourceType = PermissionBuilder.buildResourceType(permission.resourceTypeElements());
//                }
//                permissionStr = PermissionBuilder.buildPermission(resourceType, permission.action(), permission.locator());
//            }
//            MatchValuePolicy policy = permission.policy();
//            if (policy != MatchValuePolicy.NONE) {
//                if (context == null) {
//                    context = AspectExpressUtil.buildContext(method, joinPoint.getArgs());
//                }
//                if (policy == MatchValuePolicy.PARAMS) {
//                    //通过SPEL解析表达式 当前只是从参数中获取
//                    permissionStr = AspectExpressUtil.getValue(permissionStr, context);
//                }
//                if (policy == MatchValuePolicy.HANDLER) {
//                    //还未考虑好
//                    throw new UnsupportedOperationException("not  supported");
//                }
//            }
//            String enableExpression = permission.enable();
//            boolean enable = Boolean.parseBoolean(enableExpression);
//            if (!enable) {
//                if (context == null) {
//                    context = AspectExpressUtil.buildContext(method, joinPoint.getArgs());
//                }
//                enable = AspectExpressUtil.getValue(enableExpression, context, Boolean.TYPE);
//            }
//            if (enable) {
//                permissionList.add(permissionStr);
//            }
//        }
//        //匹配权限串
//        PermissionUtils.verifyPermission(permissionList.toArray(new String[]{}));
//        return joinPoint.proceed();
//    }
//
//    /**
//     * 获取方法数据
//     */
//    private MetaData getMetaData(MethodSignature signature, ProceedingJoinPoint joinPoint) {
//        String signatureStr = signature.toString();
//        return Optional.ofNullable(METADATA_MAP.get(signatureStr))
//                .orElseGet(() -> METADATA_MAP.computeIfAbsent(signatureStr, (key) -> {
//                    Method method = signature.getMethod();
//                    //可能会存在部分情况签名是接口方法，而不是具体的实现方法的情况，保险起见获取切点代理类
//                    Class<?> clazz = joinPoint.getTarget().getClass();
//                    try {
//                        method = clazz.getMethod(method.getName(), method.getParameterTypes());
//                    } catch (NoSuchMethodException e) {
//                        log.error("", e);
//                    }
//                    Permission[] permissions = method.getAnnotationsByType(Permission.class);
//                    return new MetaData().setMethod(method).setPermissions(permissions);
//                }));
//    }
//
//    @Data
//    @Accessors(chain = true)
//    private static final class MetaData {
//        private Method method;
//        private Permission[] permissions;
//    }
//
//}
