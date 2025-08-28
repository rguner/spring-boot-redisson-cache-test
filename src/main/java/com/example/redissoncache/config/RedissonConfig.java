package com.example.redissoncache.config;

import com.google.common.cache.CacheBuilder;
import org.redisson.Redisson;
import org.redisson.api.LocalCachedMapOptions;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.CacheConfig;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

@Configuration
public class RedissonConfig {

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redissonClient() {
        String[] redisNodeAddresses = "redis://10.220.14.125:6380,redis://10.220.14.126:6380,redis://10.220.14.127:6380".split(",");

        Config config = new Config();
        config.useClusterServers()
                .setClientName("spring-boot-redisson-cache-test")
                .addNodeAddress(redisNodeAddresses)
                .setPassword("redispass86")         // Set if required
                .setRetryAttempts(3)
                .setRetryInterval(2000)             // 2 seconds
                .setMasterConnectionPoolSize(16)    // Adjust as needed
                .setSlaveConnectionPoolSize(16)
                .setMasterConnectionMinimumIdleSize(4)                  // Use default value
                .setSlaveConnectionMinimumIdleSize(4)                  // Use default value
                .setCheckSlotsCoverage(false);// Adjust as needed

        return Redisson.create(config);

    }

    @Bean
    @Primary
    public CacheManager cacheManager(RedissonClient redissonClient) {
        Map<String, CacheConfig> config = new HashMap<>();
        
        // Cache for user data with 10 minutes TTL
        //config.put("spring-boot-redisson-cache-test-userCache", new CacheConfig(600000, 300000));
        
        // Cache for product data with 5 minutes TTL
        //config.put("spring-boot-redisson-cache-test-productCache", new CacheConfig(300000, 150000));
        
        // Local cache configuration for frequently accessed data
        //config.put("spring-boot-redisson-cache-test-localCache", new CacheConfig(60000, 30000));

        config.put("spring-boot-redisson-cache-test-productCache-all", new CacheConfig(60000, 150000));

        return new RedissonSpringCacheManager(redissonClient, config);
    }


    @Bean(name = "localCacheManager")
    public CacheManager localCacheManager() {
        return new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(final String name) {
                ConcurrentMap<Object, Object> map = CacheBuilder.newBuilder().expireAfterWrite(20, TimeUnit.MINUTES)
                        .maximumSize(100).build().asMap();
                return new ConcurrentMapCache(name, map, false);
            }
        };
    }


}
