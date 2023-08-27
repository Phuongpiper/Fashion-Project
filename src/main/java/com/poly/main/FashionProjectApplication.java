package com.poly.main;	

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = {"com.poly.controller","com.poly.model","com.poly.repository","com.poly.config",
        "com.poly.entity","com.poly.service"})
@EnableJpaRepositories("com.poly.repository")
@EntityScan("com.poly.entity")
public class FashionProjectApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(FashionProjectApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(FashionProjectApplication.class);
    }
}
