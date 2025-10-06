package com.example.portal;

import com.example.portal.config.ProducerProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(ProducerProperties.class)
public class SsoPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(SsoPortalApplication.class, args);
    }
}
