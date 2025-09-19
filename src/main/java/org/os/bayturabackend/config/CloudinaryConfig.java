package org.os.bayturabackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudinaryConfig {

    @Value("${cloudinary.cloud-name}")
    private String cloudName;

    @Value("${cloudinary.api-key}")
    private String apiKey;

    @Value("${cloudinary.api-secret}")
    private String apiSecret;

    @Bean
    public com.cloudinary.Cloudinary cloudinary() {
        return new com.cloudinary.Cloudinary(
                String.format("cloudinary://%s:%s@%s", apiKey, apiSecret, cloudName)
        );
    }
}
