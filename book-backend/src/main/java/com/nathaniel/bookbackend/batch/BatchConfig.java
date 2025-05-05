package com.nathaniel.bookbackend.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.nathaniel.bookbackend.models.Recommendation;
import com.nathaniel.bookbackend.models.UserBook;
import org.springframework.transaction.PlatformTransactionManager;


@Configuration
// @EnableBatchProcessing is deprecated since Spring Batch 5 and can be removed
// if using Spring Boot 3 auto-configuration for Batch.
// Keeping it for now if not on Boot 3 yet or explicit configuration is desired.
@EnableBatchProcessing
public class BatchConfig {
    @Bean
    public Job recJob(JobRepository jobRepository, Step computeStep) {
        return new JobBuilder("computeRecommendations", jobRepository)
                .start(computeStep)
                .build();
    }

    @Bean
    public Step computeStep(JobRepository jobRepository,
                            PlatformTransactionManager transactionManager,
                            ItemReader<UserBook> reader,
                            ItemProcessor<UserBook, Recommendation> proc,
                            ItemWriter<Recommendation> writer) {
        return new StepBuilder("computeStep", jobRepository)
                .<UserBook, Recommendation>chunk(100, transactionManager)
                .reader(reader).processor(proc).writer(writer)
                .build();
    }
}