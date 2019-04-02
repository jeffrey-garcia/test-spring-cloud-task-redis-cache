package com.example.jeffrey.springcloudtask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringCloudTaskApplication {
	private static Logger LOGGER = LoggerFactory.getLogger(SpringCloudTaskApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringCloudTaskApplication.class, args);
	}

}
