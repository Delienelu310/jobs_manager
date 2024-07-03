package com.ilumusecase.jobs_manager.s3clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.minio.MinioClient;

@Configuration
public class S3Configuration {

    @Value("${minio.endpoint}")
    private String endpoint;
    @Value("${minio.username}")
    private String username;
    @Value("${minio.password}")
    private String password;

    @Bean
    public MinioClient minioClient(){
        return MinioClient.builder()
            .endpoint(endpoint)
            .credentials(username, password)
            
            .build();
    }
}
