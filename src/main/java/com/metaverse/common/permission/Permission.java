package com.metaverse.common.permission;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
    // 资源类型集合
    String[] resourceTypeElements() default {};

    // 动作
    String action() default "";

    // 定位符
    String locator() default "";
}