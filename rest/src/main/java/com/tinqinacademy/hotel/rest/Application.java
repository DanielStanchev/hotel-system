package com.tinqinacademy.hotel.rest;


import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;


@Slf4j
@ComponentScan(basePackages = "com.tinqinacademy.hotel")
@EntityScan(basePackages = "com.tinqinacademy.hotel.persistence.entity")
@EnableJpaRepositories(basePackages = "com.tinqinacademy.hotel.persistence.repository")
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
