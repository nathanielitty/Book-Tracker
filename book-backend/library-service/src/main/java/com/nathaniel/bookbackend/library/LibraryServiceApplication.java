package com.nathaniel.bookbackend.library;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableFeignClients
@EnableKafka
@ComponentScan(basePackages = {
    "com.nathaniel.bookbackend.library",
    "com.nathaniel.bookbackend.common"
}, excludeFilters = @ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.REGEX, pattern = "com\\.nathaniel\\.bookbackend\\.parentconfig\\.SecurityConfig"))
public class LibraryServiceApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(LibraryServiceApplication.class, args);
    }
}
