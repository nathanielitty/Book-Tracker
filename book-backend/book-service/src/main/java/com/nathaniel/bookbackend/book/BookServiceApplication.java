package com.nathaniel.bookbackend.book;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.nathaniel.bookbackend.book",
    "com.nathaniel.bookbackend.common"
}, excludeFilters = @ComponentScan.Filter(type = org.springframework.context.annotation.FilterType.REGEX, pattern = "com\\.nathaniel\\.bookbackend\\.parentconfig\\.SecurityConfig"))
public class BookServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BookServiceApplication.class, args);
    }
}
