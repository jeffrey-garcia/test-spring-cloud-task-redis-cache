package com.example.jeffrey.springcloudtask.configuration;

import com.example.jeffrey.springcloudtask.SpringCloudTaskApplication;
import com.example.jeffrey.springcloudtask.db.CacheEntity;
import com.example.jeffrey.springcloudtask.db.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Date;

@Configuration
@EnableBatchProcessing
@EnableTask
public class TaskConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(SpringCloudTaskApplication.class);

    // define all the beans here

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    public CacheService cacheService;

    @Bean
    public Job job1() {
        return jobBuilderFactory.get("job1")
                // Need an unique incrementer because jobs use a database to maintain execution state
                // Spring Batch has the rule that a JobInstance can only be run once to completion.
                // This means that for each combination of identifying job parameters, only have one
                // JobExecution that can results in COMPLETE.
                .incrementer(new RunIdIncrementer())
                .start(stepBuilderFactory.get("job1step1")
                        .tasklet((contribution, chunkContext) -> {
                            LOGGER.info("Job1 was run");

                            // update the value to cache
                            cacheService.save(new CacheEntity("test1-key", new Date().toString()));
                            cacheService.save(new CacheEntity("test2-key", new Date().toString()));
                            cacheService.save(new CacheEntity("test3-key", new Date().toString()));
                            cacheService.save(new CacheEntity("test4-key", new Date().toString()));

                            Iterable<CacheEntity> records = cacheService.findAll();
                            LOGGER.info(records.toString());

                            return RepeatStatus.FINISHED;
                        })
                        .build())
                .build();
    }

}
