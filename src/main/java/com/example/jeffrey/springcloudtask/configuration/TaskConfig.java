package com.example.jeffrey.springcloudtask.configuration;

import com.example.jeffrey.springcloudtask.SpringCloudTaskApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Date;
import java.util.List;

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
    public Job job() {
        return jobBuilderFactory.get(getJobName())
                // Need an unique incrementer because jobs use a database to maintain execution state
                // Spring Batch has the rule that a JobInstance can only be run once to completion.
                // This means that for each combination of identifying job parameters, only have one
                // JobExecution that can results in COMPLETE.
//                .incrementer(new RunIdIncrementer())
                .incrementer(new SimpleIncrementer())
                .start(stepBuilderFactory.get("job1step1")
                        .tasklet((contribution, chunkContext) -> {
                            LOGGER.info("Job1 was run");

                            // TODO: replace the lengthy computation work
//                            LengthyWork.testSherlockAndAnagrams();

                            // Update the result to remote cache via restful call
//                            URI uri = URI.create(cacheServiceConfig.writerEndpoint);
//                            Map<String, String> entity = new HashMap<>();
//                            entity.put("endpoint", "test4-key");
//                            entity.put("responseBody", new Date().toString());
//                            ResponseEntity<?> responseEntity = restTemplate.postForEntity(uri, entity, Map.class);
//                            LOGGER.info("response: {}", responseEntity.getBody().toString());

                            URI uri = URI.create("http://localhost:8081/customers");
                            List<Object> objectList = restTemplate.exchange(uri, HttpMethod.GET, null, new ParameterizedTypeReference<List<Object>>() {}).getBody();

                            return RepeatStatus.FINISHED;
                        })
                        .build())
                .build();
    }

    protected String getJobName() {
        String jobName = "job-" + System.currentTimeMillis();
        return jobName;
    }

    class SimpleIncrementer implements JobParametersIncrementer {
        public JobParameters getNext(JobParameters parameters) {
            long id = 1L;

            if (parameters!=null && !parameters.isEmpty()) {
                id = parameters.getLong("run.id",1L) + 1;
            }

            return new JobParametersBuilder()
                    .addLong("run.id", id)
                    .addParameter("timestamp", new JobParameter(new Date().toString()))
                    .toJobParameters();
        }
    }
}
