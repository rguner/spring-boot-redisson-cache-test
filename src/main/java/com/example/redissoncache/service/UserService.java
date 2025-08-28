package com.example.redissoncache.service;

import com.example.redissoncache.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    
    // Simulate database storage
    private final Map<Long, User> userDatabase = new HashMap<>();

    public UserService() {
        // Initialize with some test data
        userDatabase.put(1L, new User(1L, "John Doe", "john.doe@example.com"));
        userDatabase.put(2L, new User(2L, "Jane Smith", "jane.smith@example.com"));
        userDatabase.put(3L, new User(3L, "Bob Johnson", "bob.johnson@example.com"));
    }

    @Cacheable(value = "spring-boot-redisson-cache-test-userCache", key = "#id")
    public User getUserById(Long id) {
        logger.info("Fetching user from database with ID: {}", id);
        // Simulate database delay
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        User user = userDatabase.get(id);
        if (user != null) {
            user.setLastAccessed(LocalDateTime.now());
        }
        return user;
    }

    @CachePut(value = "spring-boot-redisson-cache-test-userCache", key = "#user.id")
    public User updateUser(User user) {
        logger.info("Updating user in database: {}", user.getId());
        // Simulate database operation
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        userDatabase.put(user.getId(), user);
        return user;
    }

    @CacheEvict(value = "spring-boot-redisson-cache-test-userCache", key = "#id")
    public void deleteUser(Long id) {
        logger.info("Deleting user from database with ID: {}", id);
        userDatabase.remove(id);
    }

    @CacheEvict(value = "spring-boot-redisson-cache-test-userCache", allEntries = true)
    public void clearAllUsers() {
        logger.info("Clearing all users from cache and database");
        userDatabase.clear();
    }

    // Method for testing local cache
    @Cacheable(value = "spring-boot-redisson-cache-test-localCache", key = "#id")
    public String getUserNameLocal(Long id) {
        logger.info("Fetching user name from local cache for ID: {}", id);
        User user = userDatabase.get(id);
        return user != null ? user.getName() : null;
    }

    public Map<Long, User> getAllUsers() {
        return new HashMap<>(userDatabase);
    }
}
