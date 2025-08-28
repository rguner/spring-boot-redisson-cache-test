package com.example.redissoncache.controller;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private RedissonClient redissonClient;

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getCacheStats() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get cache names
        stats.put("cacheNames", cacheManager.getCacheNames());
        
        // Get Redis connection info
        stats.put("redisConnected", redissonClient.getNodesGroup().pingAll());
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/clear-all")
    public ResponseEntity<String> clearAllCaches() {
        cacheManager.getCacheNames().forEach(cacheName -> {
            cacheManager.getCache(cacheName).clear();
        });
        return ResponseEntity.ok("All caches cleared successfully");
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, String>> getCacheInfo() {
        Map<String, String> info = new HashMap<>();
        info.put("cacheManager", cacheManager.getClass().getSimpleName());
        info.put("redissonClient", redissonClient.getClass().getSimpleName());
        info.put("description", "Redisson Cache Manager with TTL configuration");
        return ResponseEntity.ok(info);
    }
}
