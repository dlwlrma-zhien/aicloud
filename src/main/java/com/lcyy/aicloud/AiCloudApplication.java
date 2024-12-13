package com.lcyy.aicloud;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lcyy.aicloud.mapper")
public class AiCloudApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiCloudApplication.class, args);
    }

}
