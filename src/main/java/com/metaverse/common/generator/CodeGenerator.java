package com.metaverse.common.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

public class CodeGenerator {

    public static void main(String[] args) {
        // 数据库连接信息
        String url = "jdbc:mysql://localhost:3306/mysql_test";
        String username = "root";
        String password = "Hainui";

        // 使用 FastAutoGenerator 进行自动化代码生成
        FastAutoGenerator.create(url, username, password)
                .globalConfig(builder -> {
                    builder.author("Hainui") // 设置作者
                            .enableSwagger() // 开启 swagger 模式
                            .outputDir("src/main/java") // 指定输出目录
                            .dateType(DateType.TIME_PACK) // 设置日期类型为 LocalDateTime
                            .disableOpenDir() // 不打开输出目录
                            .commentDate("yyyy-MM-dd HH:mm:ss"); // 设置注释日期格式
                })
                .packageConfig(builder -> {
                    builder.parent("com.metaverse.user") // 设置父包名
                            .moduleName("db") // 设置父包模块名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "src/main/resources/mapper/user")); // 设置 XML 生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder()
                            .enableLombok() // 开启 Lombok
                            .enableRemoveIsPrefix() // 开启驼峰转下划线字段名
                            .naming(NamingStrategy.underline_to_camel) // 设置命名策略
                            .formatFileName("%sDO"); // 设置实体类的文件名格式
                    builder.controllerBuilder().enableRestStyle().enableHyphenStyle(); // RESTful 风格控制器
                    builder.addInclude("metaverse_user_friend,metaverse_user_friend_operation_log,metaverse_friend_request,metaverse_user_friend_question,metaverse_user_group,metaverse_user_group_member,metaverse_group_operation_log,metaverse_group_question,metaverse_group_join_request,metaverse_group_invitation,metaverse_chat_record,metaverse_group_chat_record") // 设置表前缀
                            .mapperBuilder()
                            .enableBaseResultMap() // 启用基本的结果映射
                            .enableBaseColumnList() // 启用基本的列列表
                            .enableMapperAnnotation(); // 启用 Mapper 注解
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 引擎
                .execute();
    }
}