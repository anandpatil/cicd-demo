package com.cicd.userservice.service;

import com.cicd.userservice.model.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {

    private final Map<String, User> users = new HashMap<>();

    public UserService() {
        users.put("1", new User("1", "John Doe", "john@example.com"));
        users.put("2", new User("2", "Jane Smith", "jane@example.com"));
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public Optional<User> getUserById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public Map<String, String> health() {
        Map<String, String> status = new HashMap<>();
        status.put("status", "UP");
        status.put("service", "user-service");
        return status;
    }
}
