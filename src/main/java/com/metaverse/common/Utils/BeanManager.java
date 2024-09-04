package com.metaverse.common.Utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class BeanManager implements ApplicationContextAware {


    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        BeanManager.applicationContext = applicationContext;
    }

    /**
     * 根据Class类型获取Bean
     *
     * @param clazz Bean的类型
     * @param <T>   Bean的泛型类型
     * @return 返回指定类型的Bean
     */
    public static <T> T getBean(Class<T> clazz) {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext has not been initialized.");
        }
        return applicationContext.getBean(clazz);
    }

    /**
     * 根据Bean的名称获取Bean
     *
     * @param name Bean的名称
     * @param <T>  Bean的泛型类型
     * @return 返回指定名称的Bean
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        if (applicationContext == null) {
            throw new IllegalStateException("ApplicationContext has not been initialized.");
        }
        return applicationContext.getBean(name, clazz);
    }
}