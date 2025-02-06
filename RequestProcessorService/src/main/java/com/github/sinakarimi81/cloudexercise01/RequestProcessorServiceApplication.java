package com.github.sinakarimi81.cloudexercise01;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class RequestProcessorServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(RequestProcessorServiceApplication.class, args);
    }

}
