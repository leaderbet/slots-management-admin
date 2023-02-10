package com.leaderbet.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "sma")
public class ConfigProps {
    private String minioEndpoint;
    private String minioUser;
    private String minioPassword;
    private String minioBucket;
}
