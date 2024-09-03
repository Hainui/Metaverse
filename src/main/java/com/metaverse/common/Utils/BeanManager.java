package com.metaverse.common.Utils;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.util.Map;

public class BeanManager {

    private static final ApplicationContext context;

    static {
        // 初始化ApplicationContext
        context = new ClassPathXmlApplicationContext("applicationContext.xml");
    }

    /**
     * 获取指定类型的单个bean实例。
     *
     * @param clazz bean的类型
     * @param <T>   泛型类型
     * @return 返回指定类型的bean实例
     */
    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    /**
     * 获取所有指定类型的bean实例。
     *
     * @param clazz bean的类型
     * @param <T>   泛型类型
     * @return 返回所有指定类型的bean实例的映射
     */
    public static <T> Map<String, T> getBeansOfType(Class<T> clazz) {
        return context.getBeansOfType(clazz);
    }
}