package com.leaderbet.config;

import io.minio.MinioClient;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@AllArgsConstructor
public class config {
    private final ConfigProps configProps;

    @Bean
    public MinioClient minioClient() {
        return MinioClient.builder()
                .endpoint(configProps.getMinioEndpoint())
                .credentials(configProps.getMinioUser(), configProps.getMinioPassword())
                .build();
    }
}
