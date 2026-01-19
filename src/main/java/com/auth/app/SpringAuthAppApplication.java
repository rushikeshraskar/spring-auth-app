package com.auth.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"com.auth.app"})
@EnableJpaRepositories(basePackages = {"com.auth.app.repository"})
public class SpringAuthAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAuthAppApplication.class, args);
    }
}
