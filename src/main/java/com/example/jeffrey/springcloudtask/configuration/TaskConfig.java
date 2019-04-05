package com.example.jeffrey.springcloudtask.configuration;

import com.example.jeffrey.springcloudtask.SpringCloudTaskApplication;
import com.example.jeffrey.springcloudtask.computation.LengthyWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@EnableTask
@EnableConfigurationProperties(value = { CacheServiceConfig.class})
public class TaskConfig {
    private static Logger LOGGER = LoggerFactory.getLogger(SpringCloudTaskApplication.class);

    // define all the beans here

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CacheServiceConfig cacheServiceConfig;

    @Bean
    public Job job1() {
        return jobBuilderFactory.get("job1")
                // Need an unique incrementer because jobs use a database to maintain execution state
                // Spring Batch has the rule that a JobInstance can only be run once to completion.
                // This means that for each combination of identifying job parameters, only have one
                // JobExecution that can results in COMPLETE.
                .incrementer(new RunIdIncrementerWithSystemTime())
                .start(stepBuilderFactory.get("job1step1")
                        .tasklet((contribution, chunkContext) -> {
                            LOGGER.info("Job1 was run");

                            // TODO: replace the lengthy computation work
                            LengthyWork.testSherlockAndAnagrams();

                            // Update the result to remote cache via restful call
                            URI uri = URI.create(cacheServiceConfig.writerEndpoint);
                            Map<String, String> entity = new HashMap<>();
                            entity.put("endpoint", "test4-key");
                            entity.put("responseBody", new Date().toString());
                            ResponseEntity<?> responseEntity = restTemplate.postForEntity(uri, entity, Map.class);
                            LOGGER.info("response: {}", responseEntity.getBody().toString());

                            return RepeatStatus.FINISHED;
                        })
                        .build())
                .build();
    }

}

class RunIdIncrementerWithSystemTime extends RunIdIncrementer {
    private static String RUN_ID_KEY = "run.id";
    private String key;
    private String systemTimeSuffix = "run.systemTime";

    public RunIdIncrementerWithSystemTime() {
        this.key = RUN_ID_KEY;
    }

    @Override
    public void setKey(String key) {
        this.key = key;
        super.setKey(key);
    }

    @Override
    public JobParameters getNext(JobParameters parameters) {
        JobParameters params = parameters == null ? new JobParameters() : parameters;
        long id = params.getLong(this.key, 0L) + 1L;
        long currentTimeInMillis = parameters.getLong(this.key, System.currentTimeMillis());
        return (new JobParametersBuilder(params))
                .addLong(this.key, id)
                .addLong(systemTimeSuffix, currentTimeInMillis)
                .toJobParameters();
    }
}