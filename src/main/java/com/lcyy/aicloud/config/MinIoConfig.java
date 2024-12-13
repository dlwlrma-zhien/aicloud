package com.lcyy.aicloud.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author: dlwlrma
 * @data 2024年10月28日 21:02
 * @Description: TODO:分布式MinIO服务器配置类
 */
@Configuration
public class MinIoConfig {
    //获取服务器地址
    @Value("${minio.endpoint}")
    private String endpoint;

    //获取accessKey
    @Value("${minio.accessKey}")
    private String accessKey;

    //获取secretKey
    @Value("${minio.secretKey}")
    private String secretKey;

    //将本地服务器注册进来
    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }
}
