# A demo project for Spring Cloud Task
A spring-boot application that runs as a short-lived microservice using Spring Cloud Task.

<br/>

### Motivation:
- Spring Cloud Task makes it easy to create short-lived microservices. 
- It provides capabilities that let short lived JVM processes be executed 
on demand in a production environment.
- So we gtt the flexibility of running any task dynamically, allocating 
resources on demand and retrieving the results after the Task completion.

<br/>

### Creating a simple Spring Cloud Task
A Spring Cloud Task is simply made up of a Spring Boot application that is 
expected to end. For this example, we need only to add a single additional 
dependency — the one for Spring Cloud Task itself:
```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-task</artifactId>
    <version>1.2.3.RELEASE</version>
</dependency>
```

<br/>

Create a single Java file and annotates the class with `@SpringBootApplication` 
and `@EnableTask`, and implement the operation of the task in 
the `CommandLineRunner` 
```java
@SpringBootApplication
@EnableTask
public class SimpleCloudTaskApp {
	//...
	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) throws Exception {
		return args -> {
			// implement the operation of the task
		};
	}
	//...
}
```
- The class-level @EnableTask annotation tells Spring Cloud Task to bootstrap 
it’s functionality
- By default, it imports an additional configuration class 
(SimpleTaskConfiguration). This additional configuration registers the 
TaskRepository and the infrastructure for its use.

<br/>

### Spring Batch
Spring Batch is lightweight, comprehensive batch framework designed to enable the development 
of robust batch applications vital for the daily operations of enterprise systems. It provides: 
- reusable functions that are essential in processing large volumes of records, 
including: 
    - logging/tracing 
    - transaction management 
    - chunk based processing
    - declarative I/O
    - job processing statistics 
    - job start, stop, restart, retry and skip 
    - resource management
    - web based administration interface (via Spring Cloud Data Flow)
- more advanced technical services and features that will enable extremely 
high-volume and high performance batch jobs through optimization and partitioning 
techniques. 
- simple as well as complex, high-volume batch jobs can leverage the framework 
in a highly scalable manner to process significant volumes of information.

<br/>

### Spring Cloud Task’s integration with Spring Batch
We can execute Spring Batch Job as a Task and log events of the job execution using 
Spring Cloud Task. To enable this feature we need to add Batch dependencies pertaining 
to Boot and Cloud:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-batch</artifactId>
</dependency>
```

To configure a job as a Task we need to have the Job bean registered in the JobConfiguration class:
```java
@SpringBootApplication
@EnableTask
@EnableBatchProcessing
public class SimpleCloudTaskApp {
    // ...
    @Bean
    public Job job() {
        return jobBuilderFactory.get("job")
          .start(stepBuilderFactory.get("jobStep1")
          .tasklet(new Tasklet(){
              @Override
              public RepeatStatus execute(
                StepContribution contribution,
                ChunkContext chunkContext) throws Exception {
                    // define the operation of the batch here
                    // ...
                    return RepeatStatus.FINISHED;
              }
        }).build()).build();
    }
    // ...
}
```
- The `@EnableBatchProcessing` annotation adds many critical beans required to set up 
batch jobs and will saves us a lot of leg work. 
- It will trigger the Spring Batch Job execution and Spring Cloud Task will log the 
events of the executions of all batch jobs with the other Task executed in the spring 
cloud database (Spring Cloud Data Flow Server).
<br/>

### Deploy artifactory to Maven repoitory
To maximize the re-usability of the cloud task/batch application, deploy the binary (jar)
to the designated Maven repository.

Sample Maven configuration:
```xml
	<!-- Deploy to in-house repository server when test passed -->
	<distributionManagement>
		<repository>
			<id>releases</id>
			<url>${artifactory.url}/libs-release-local/</url>
		</repository>
	</distributionManagement>
```

<br/>

### References:

##### Spring Cloud Task 1.2.2
- http://cloud.spring.io/spring-cloud-task/
- https://docs.spring.io/spring-cloud-task/docs/1.2.2.RELEASE/reference/htmlsingle/

##### Spring Batch
- https://projects.spring.io/spring-batch/
- https://spring.io/guides/gs/batch-processing/


<br/>