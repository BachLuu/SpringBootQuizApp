package com.example.SpringBootWeb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.SpringBootWeb.entities.jwt.JwtProperties;

@SpringBootApplication
@ComponentScan(basePackages = "com.example.SpringBootWeb")
@EnableJpaRepositories(basePackages = "com.example.SpringBootWeb.repositories")
public class SpringBootWebApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebApplication.class, args);
	}

}
