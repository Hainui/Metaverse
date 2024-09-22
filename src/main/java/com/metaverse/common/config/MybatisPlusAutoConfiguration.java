package com.metaverse.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


/**
 * @Description
 * @Author hainui
 */
@MapperScan(basePackages = {"com.metaverse.region.db.mapper", "com.metaverse.user.db.mapper", "com.metaverse.permission.db.mapper", "com.metaverse.file.db.mapper"})
@ComponentScan(basePackages = {"com.metaverse.region.db.service", "com.metaverse.user.db.service", "com.metaverse.permission.db.service", "com.metaverse.file.db.service"})
@Configuration
public class MybatisPlusAutoConfiguration {
}
