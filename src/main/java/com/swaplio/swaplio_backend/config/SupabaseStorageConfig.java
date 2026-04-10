package com.swaplio.swaplio_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class SupabaseStorageConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}