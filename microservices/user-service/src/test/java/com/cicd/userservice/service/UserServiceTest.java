package com.cicd.userservice.service;

import com.cicd.userservice.model.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserServiceTest {

    @Test
    void defaultUsersAreAvailable() {
        UserService userService = new UserService();

        List<User> users = userService.getAllUsers();

        assertEquals(2, users.size());
    }

    @Test
    void createUserPersistsAndCanBeFetched() {
        UserService userService = new UserService();
        User user = new User("9", "Test User", "test@example.com");

        userService.createUser(user);
        Optional<User> fetched = userService.getUserById("9");

        assertTrue(fetched.isPresent());
        assertEquals("Test User", fetched.get().getName());
    }

    @Test
    void getUserByIdReturnsEmptyForUnknownId() {
        UserService userService = new UserService();

        Optional<User> user = userService.getUserById("missing");

        assertFalse(user.isPresent());
    }

    @Test
    void healthReturnsUpStatus() {
        UserService userService = new UserService();

        Map<String, String> health = userService.health();

        assertEquals("UP", health.get("status"));
        assertEquals("user-service", health.get("service"));
    }
}
