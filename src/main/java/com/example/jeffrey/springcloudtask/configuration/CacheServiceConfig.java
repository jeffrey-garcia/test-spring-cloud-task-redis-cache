package com.example.jeffrey.springcloudtask.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache-service")
public class CacheServiceConfig {

    String writerEndpoint;

    public String getWriterEndpoint() {
        return this.writerEndpoint;
    }

    public void setWriterEndpoint(String writerEndpoint) {
        this.writerEndpoint = writerEndpoint;
    }
}
