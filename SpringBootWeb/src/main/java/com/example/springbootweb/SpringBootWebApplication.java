package com.example.springbootweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.springbootweb.entities.jwt.JwtProperties;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.springbootweb.repositories")
@EnableConfigurationProperties(JwtProperties.class)
@EnableFeignClients
public class SpringBootWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootWebApplication.class, args);
    }

}
