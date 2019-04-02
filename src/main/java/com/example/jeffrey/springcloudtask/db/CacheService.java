package com.example.jeffrey.springcloudtask.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private CacheRepository cacheRepository;

    @Autowired
    public CacheService(CacheRepository cacheRepository) {
        this.cacheRepository = cacheRepository;
    }

    public Iterable<CacheEntity> findAll() {
        return cacheRepository.findAll();
    }

    public long count() {
        return cacheRepository.count();
    }

    public CacheEntity findById(String endpoint) {
        return cacheRepository.findOne(endpoint);
    }

    public void save(CacheEntity entity) {
        cacheRepository.save(entity);
    }

    public void deleteAll() {
        cacheRepository.deleteAll();
    }

}

