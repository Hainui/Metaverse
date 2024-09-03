package com.metaverse.login.generator;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
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
                           .fileOverride() // 覆盖已生成文件
                           .outputDir("src/main/java/com/metaverse/login"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent(null) // 设置父包名
                           .moduleName(null) // 设置父包模块名
                           .pathInfo(Collections.singletonMap(OutputFile.xml, "src/main/resources/mapper")); // 设置 XML 生成路径
                })
                .strategyConfig(builder -> {
                    builder.entityBuilder().enableLombok() // 开启 Lombok
                    .controllerBuilder().enableRestStyle().enableHyphenStyle(); // RESTful 风格控制器
                    builder.addInclude("region", "metaverse_user") // 设置表前缀
                            .entityBuilder()
                            .enableLombok() // 开启 Lombok
                            .enableRemoveIsPrefix() // 开启驼峰转下划线字段名
                            .naming(NamingStrategy.underline_to_camel) // 设置命名策略
                            .formatFileName("%sDO"); // 设置实体类的文件名格式
                })
                .templateEngine(new FreemarkerTemplateEngine()) // 使用 Freemarker 引擎
                .execute();
    }
}