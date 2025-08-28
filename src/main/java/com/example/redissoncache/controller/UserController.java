package com.example.redissoncache.controller;

import com.example.redissoncache.model.User;
import com.example.redissoncache.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return user != null ? ResponseEntity.ok(user) : ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        User updatedUser = userService.updateUser(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/cache/clear")
    public ResponseEntity<String> clearCache() {
        userService.clearAllUsers();
        return ResponseEntity.ok("Cache cleared successfully");
    }

    @GetMapping("/{id}/name/local")
    public ResponseEntity<String> getUserNameFromLocalCache(@PathVariable Long id) {
        String userName = userService.getUserNameLocal(id);
        return userName != null ? ResponseEntity.ok(userName) : ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public ResponseEntity<Map<Long, User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }
}
