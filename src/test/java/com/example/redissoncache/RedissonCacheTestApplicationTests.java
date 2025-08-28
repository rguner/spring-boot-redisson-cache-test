package com.example.redissoncache;

import com.example.redissoncache.model.User;
import com.example.redissoncache.model.Product;
import com.example.redissoncache.service.UserService;
import com.example.redissoncache.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.redis.host=localhost",
    "spring.redis.port=6379"
})
class RedissonCacheTestApplicationTests {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    void testCacheableAnnotation() {
        // First call - should hit database
        long startTime = System.currentTimeMillis();
        User user1 = userService.getUserById(1L);
        long firstCallTime = System.currentTimeMillis() - startTime;
        
        assertNotNull(user1);
        assertEquals("John Doe", user1.getName());
        assertTrue(firstCallTime > 1500); // Should take time due to simulated delay

        // Second call - should hit cache
        startTime = System.currentTimeMillis();
        User user2 = userService.getUserById(1L);
        long secondCallTime = System.currentTimeMillis() - startTime;
        
        assertNotNull(user2);
        assertEquals("John Doe", user2.getName());
        assertTrue(secondCallTime < 100); // Should be much faster from cache
    }

    @Test
    void testCachePutAnnotation() {
        // Get user first
        User user = userService.getUserById(2L);
        assertNotNull(user);
        
        // Update user - this should update the cache
        user.setName("Jane Updated");
        User updatedUser = userService.updateUser(user);
        
        assertEquals("Jane Updated", updatedUser.getName());
        
        // Get user again - should return updated value from cache
        User cachedUser = userService.getUserById(2L);
        assertEquals("Jane Updated", cachedUser.getName());
    }

    @Test
    void testCacheEvictAnnotation() {
        // First, ensure user is cached
        User user = userService.getUserById(3L);
        assertNotNull(user);
        
        // Delete user - this should evict from cache
        userService.deleteUser(3L);
        
        // Get user again - should return null since deleted
        User deletedUser = userService.getUserById(3L);
        assertNull(deletedUser);
    }

    @Test
    void testLocalCache() {
        // Test local cache functionality
        long startTime = System.currentTimeMillis();
        String userName1 = userService.getUserNameLocal(1L);
        long firstCallTime = System.currentTimeMillis() - startTime;
        
        assertNotNull(userName1);
        
        // Second call should be faster due to local cache
        startTime = System.currentTimeMillis();
        String userName2 = userService.getUserNameLocal(1L);
        long secondCallTime = System.currentTimeMillis() - startTime;
        
        assertEquals(userName1, userName2);
        assertTrue(secondCallTime <= firstCallTime);
    }

    @Test
    void testProductCaching() {
        // Test product caching with category
        long startTime = System.currentTimeMillis();
        var products1 = productService.getProductsByCategory("Electronics");
        long firstCallTime = System.currentTimeMillis() - startTime;
        
        assertFalse(products1.isEmpty());
        assertTrue(firstCallTime > 2500); // Should take time due to simulated delay

        // Second call should be from cache
        startTime = System.currentTimeMillis();
        var products2 = productService.getProductsByCategory("Electronics");
        long secondCallTime = System.currentTimeMillis() - startTime;
        
        assertEquals(products1.size(), products2.size());
        assertTrue(secondCallTime < 100); // Should be much faster from cache
    }

    @Test
    void testCacheManager() {
        // Verify cache manager is properly configured
        assertNotNull(cacheManager);
        assertTrue(cacheManager.getCacheNames().contains("userCache"));
        assertTrue(cacheManager.getCacheNames().contains("productCache"));
        assertTrue(cacheManager.getCacheNames().contains("localCache"));
    }
}
