//package com.metaverse.common.Utils;
//
//
//import com.google.common.collect.Lists;
//import com.google.common.collect.Sets;
//import io.bigplayers.tangerine.exception.system.PermissionDeniedException;
//import io.bigplayers.tangerine.exception.system.SystemExceptionConstant;
//import org.apache.commons.lang3.ArrayUtils;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.util.CollectionUtils;
//import org.springframework.util.PatternMatchUtils;
//import org.springframework.util.StringUtils;
//
//import java.util.Arrays;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//import java.util.function.Supplier;
//import java.util.stream.Collectors;
//
///**
// * 检测权限工具
// */
//public class PermissionUtils {
//
//
//    /**
//     * 是否匹配中其中一个权限 待匹配的权限串中的其中一个匹配即可
//     *
//     * @param patterns         权限串集合
//     * @param matchPermissions 待匹配的权限串
//     */
//    public static boolean hasMatchAnyPermission(List<String> patterns, String... matchPermissions) {
//        if (CollectionUtils.isEmpty(patterns)) {
//            return false;
//        }
//        if (ArrayUtils.isEmpty(matchPermissions)) {
//            return false;
//        }
//        return Arrays.stream(matchPermissions).parallel().filter(StringUtils::hasText)
//                .anyMatch(match -> anyMatch(patterns, match));
//    }
//
//    /**
//     * 是否匹配中其中一个权限  待匹配的权限串中的每个元素都需要匹配
//     *
//     * @param patterns         权限串集合
//     * @param matchPermissions 待匹配的权限串
//     */
//    public static boolean hasMatchAllPermission(List<String> patterns, String... matchPermissions) {
//        if (CollectionUtils.isEmpty(patterns)) {
//            return false;
//        }
//        if (ArrayUtils.isEmpty(matchPermissions)) {
//            return false;
//        }
//        return Arrays.stream(matchPermissions).parallel().filter(StringUtils::hasText)
//                .allMatch(match -> anyMatch(patterns, match));
//    }
//
//
//    /**
//     * 是否匹配中其中一个权限  待匹配的权限串中的每个元素都需要匹配
//     *
//     * @param patterns         权限串集合
//     * @param matchPermissions 待匹配的权限串
//     */
//    public static boolean hasMatchAllPermission(List<String> patterns, List<String> matchPermissions) {
//        if (CollectionUtils.isEmpty(patterns)) {
//            return false;
//        }
//        if (CollectionUtils.isEmpty(matchPermissions)) {
//            return false;
//        }
//        return matchPermissions.stream().parallel().filter(StringUtils::hasText)
//                .allMatch(match -> anyMatch(patterns, match));
//    }
//
//
//    /**
//     * 是否匹配 匹配规则中的其中一个权限
//     *
//     * @param patterns 匹配规则列表
//     * @param match    待匹配的权限串
//     */
//    public static boolean anyMatch(List<String> patterns, String match) {
//        return patterns.parallelStream().filter(StringUtils::hasText)
//                .anyMatch(ps -> PatternMatchUtils.simpleMatch(ps, match));
//    }
//
//    /**
//     * 是否有权限
//     */
//    public static boolean hasPermission(String... permissions) {
//        return hasMatchAllPermission(grantedAuthorityList(), permissions);
//    }
//
//    /**
//     * 是否有权限  匹配其中一个就可以
//     */
//    public static boolean hasAnyPermission(String... permissions) {
//        return hasMatchAnyPermission(grantedAuthorityList(), permissions);
//    }
//
//    /**
//     * 匹配权限串  其中一组匹配通过就可以
//     * 如果没匹配通过 抛出UnauthorizedException异常
//     *
//     * @param permissions 权限串
//     */
//    public static void verifyPermissionHasAny(List<String[]> permissions) {
//        //匹配权限串
//        if (permissions.parallelStream().noneMatch(PermissionUtils::hasPermission)) {
//            throw new PermissionDeniedException();
//        }
//    }
//
//    /**
//     * 匹配权限串  其中一组匹配通过就可以
//     * 如果没匹配通过 抛出UnauthorizedException异常
//     *
//     * @param permissions      权限串
//     * @param errorMsgSupplier 异常消息
//     */
//    public static void verifyPermissionHasAny(Supplier<String> errorMsgSupplier, List<String[]> permissions) {
//        //匹配权限串
//        if (permissions.parallelStream().noneMatch(PermissionUtils::hasPermission)) {
//            throw new PermissionDeniedException(SystemExceptionConstant.PERMISSION_DENIED_EXCEPTION_CODE, errorMsgSupplier.get());
//        }
//    }
//
//    /**
//     * 全部都匹配才可以
//     * 匹配权限串  如果没匹配通过 抛出UnauthorizedException异常
//     *
//     * @param permissions 权限串
//     */
//    public static void verifyPermission(String... permissions) {
//        //匹配权限串
//        if (!PermissionUtils.hasPermission(permissions)) {
//            throw new PermissionDeniedException();
//        }
//    }
//
//    /**
//     * 全部都匹配才可以
//     * 匹配权限串  如果没匹配通过 抛出UnauthorizedException异常
//     *
//     * @param errorMsgSupplier 异常消息
//     * @param permissions      权限串
//     */
//    public static void verifyPermission(Supplier<String> errorMsgSupplier, String... permissions) {
//        //匹配权限串
//        if (!PermissionUtils.hasPermission(permissions)) {
//            throw new PermissionDeniedException(SystemExceptionConstant.PERMISSION_DENIED_EXCEPTION_CODE, errorMsgSupplier.get());
//        }
//    }
//
//    /**
//     * 匹配权限串 匹配其中一个就返回
//     * 如果没匹配通过 抛出UnauthorizedException异常
//     */
//    public static void verifyPermissionHasAny(String... permissions) {
//        //匹配权限串
//        if (!hasAnyPermission(permissions)) {
//            throw new PermissionDeniedException();
//        }
//    }
//
//    /**
//     * 匹配权限串 匹配其中一个就返回
//     * 如果没匹配通过 抛出UnauthorizedException异常
//     */
//    public static void verifyPermissionHasAny(Supplier<String> errorMsgSupplier, String... permissions) {
//        //匹配权限串
//        if (!hasAnyPermission(permissions)) {
//            throw new PermissionDeniedException(SystemExceptionConstant.PERMISSION_DENIED_EXCEPTION_CODE, errorMsgSupplier.get());
//        }
//    }
//
//
//    /**
//     * 从安全上下文中获取权限串集合
//     */
//    private static List<String> grantedAuthorityList() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        return Optional.ofNullable(authentication)
//                .map(Authentication::getAuthorities)
//                .map(grantedAuthorities -> grantedAuthorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
//                .orElse(null);
//    }
//
//
//    /**
//     * 筛选出两组权限串之间的交集
//     *
//     * @param permissions        一组权限串
//     * @param anotherPermissions 另一组权限串
//     */
//    public static List<String> intersectPermissions(List<String> permissions, List<String> anotherPermissions) {
//        if (CollectionUtils.isEmpty(permissions) || CollectionUtils.isEmpty(anotherPermissions)) {
//            return Lists.newArrayList();
//        }
//        Set<String> resultPermission = Sets.newHashSet();
//        outer:
//        for (String out : permissions) {
//            for (String inner : anotherPermissions) {
//                //如果外层权限串大于内层权限，取内层权限
//                if (PatternMatchUtils.simpleMatch(out, inner)) {
//                    resultPermission.add(inner);
//
//                    //如果内层权限大于外层权限  取外层权限并跳转外层元素下一个循环
//                } else if (PatternMatchUtils.simpleMatch(inner, out)) {
//                    resultPermission.add(out);
//                    continue outer;
//                }
//            }
//        }
//        return Lists.newArrayList(resultPermission);
//    }
//
//
//}
