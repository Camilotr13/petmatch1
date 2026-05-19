package com.example.PetMatch.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.cloudinary.Cloudinary;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary(
            @Value("${cloudinary.cloud-name}") String cloudName,
            @Value("${cloudinary.api-key}") String apiKey,
            @Value("${cloudinary.api-secret}") String apiSecret) {

        if (cloudName == null || cloudName.isBlank()) {
            throw new IllegalStateException("La propiedad cloudinary.cloud-name no está definida o está vacía");
        }

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException("La propiedad cloudinary.api-key no está definida o está vacía");
        }

        if (apiSecret == null || apiSecret.isBlank()) {
            throw new IllegalStateException("La propiedad cloudinary.api-secret no está definida o está vacía");
        }

        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", cloudName);
        config.put("api_key", apiKey);
        config.put("api_secret", apiSecret);

        return new Cloudinary(config);
    }
}