package com.example.jeffrey.springcloudtask.db;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("CacheEntity")
public class CacheEntity {

    @Id
    public String endpoint;

    public String responseBody;

    public CacheEntity(String endpoint, String responseBody) {
        this.endpoint = endpoint;
        this.responseBody = responseBody;
    }

}
