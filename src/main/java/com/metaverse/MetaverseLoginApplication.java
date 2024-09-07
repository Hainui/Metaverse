package com.metaverse;

import lombok.Getter;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
public class MetaverseLoginApplication {

    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(MetaverseLoginApplication.class, args);
        MetaverseLoginApplication.setApplicationContext(context);
    }

    @Getter
    private static ApplicationContext applicationContext;


    public static void setApplicationContext(ApplicationContext applicationContext) {
        MetaverseLoginApplication.applicationContext = applicationContext;
    }


}
