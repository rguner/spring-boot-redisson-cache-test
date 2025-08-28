package com.example.redissoncache.service;

import com.example.redissoncache.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final CacheManager cacheManager;

    // Simulate database storage
    private final Map<Long, Product> productDatabase = new HashMap<>();

    public ProductService(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
        // Initialize with some test data
        productDatabase.put(1L, new Product(1L, "Laptop", "High-performance laptop", new BigDecimal("999.99"), "Electronics", 50));
        productDatabase.put(2L, new Product(2L, "Smartphone", "Latest smartphone model", new BigDecimal("699.99"), "Electronics", 100));
        productDatabase.put(3L, new Product(3L, "Coffee Mug", "Ceramic coffee mug", new BigDecimal("15.99"), "Kitchen", 200));
        productDatabase.put(4L, new Product(4L, "Book", "Programming guide", new BigDecimal("39.99"), "Books", 75));
    }

    @Cacheable(value = "spring-boot-redisson-cache-test-productCache", key = "#id")
    public Product getProductById(Long id) {
        logger.info("Fetching product from database with ID: {}", id);
        // Simulate database delay
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return productDatabase.get(id);
    }

    @Cacheable(value = "spring-boot-redisson-cache-test-productCache", key = "'category:' + #category")
    public List<Product> getProductsByCategory(String category) {
        logger.info("Fetching products from database by category: {}", category);
        // Simulate database delay
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return productDatabase.values().stream()
                .filter(product -> product.getCategory().equalsIgnoreCase(category))
                .collect(Collectors.toList());
    }

    @CachePut(value = "spring-boot-redisson-cache-test-productCache", key = "#product.id")
    public Product updateProduct(Product product) {
        logger.info("Updating product in database: {}", product.getId());
        // Simulate database operation
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        productDatabase.put(product.getId(), product);
        return product;
    }

    @CacheEvict(value = "spring-boot-redisson-cache-test-productCache", key = "#id")
    public void deleteProduct(Long id) {
        logger.info("Deleting product from database with ID: {}", id);
        productDatabase.remove(id);
    }

    @CacheEvict(value = "spring-boot-redisson-cache-test-productCache", allEntries = true)
    public void clearAllProducts() {
        logger.info("Clearing all products from cache and database");
        productDatabase.clear();
    }

    @Cacheable(value = "spring-boot-redisson-cache-test-productCache-all", key = "'all-products-key'")
    public Map<Long, Product> getAllProducts() {
        logger.info("Fetching all products from database");
        // Simulate database delay
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return new HashMap<>(productDatabase);
    }

    @Cacheable(value ="localAll" , cacheManager = "localCacheManager")
    public Map<Long, Product> getAllProductsFromLocalCache() {
        logger.info("Fetching all products from database");

        Cache cache = cacheManager.getCache("spring-boot-redisson-cache-test-productCache-all");
        if (cache != null) {
            Cache.ValueWrapper wrapper = cache.get("all-products-key");
            if (wrapper != null) {
                return (Map<Long, Product>) wrapper.get();
            }
        }

        return new HashMap<>(productDatabase);
    }
}
