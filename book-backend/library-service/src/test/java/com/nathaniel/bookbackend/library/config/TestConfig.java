package com.nathaniel.bookbackend.library.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;
import jakarta.annotation.PreDestroy;

@TestConfiguration
public class TestConfig {
    
    private final PostgreSQLContainer<?> postgres;
    private final KafkaContainer kafka;
    
    public TestConfig() {
        this.postgres = new PostgreSQLContainer<>(DockerImageName.parse("postgres:14-alpine"))
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");
        this.kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:latest"));
        
        postgres.start();
        kafka.start();
    }
    
    @Bean
    public PostgreSQLContainer<?> postgresContainer() {
        return postgres;
    }
    
    @Bean
    public KafkaContainer kafkaContainer() {
        return kafka;
    }
    
    @PreDestroy
    public void tearDown() {
        postgres.stop();
        kafka.stop();
    }
}
