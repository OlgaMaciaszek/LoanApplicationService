package com.blogspot.toomuchcoding;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.ofg.infrastructure.config.EnableMicroservice;
import com.ofg.infrastructure.healthcheck.EnableHealthCheck;

@SpringBootApplication
@EnableMicroservice
@EnableHealthCheck
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
