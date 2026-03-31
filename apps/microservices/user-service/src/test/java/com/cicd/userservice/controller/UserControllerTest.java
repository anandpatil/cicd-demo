package com.cicd.userservice.controller;

import com.cicd.userservice.model.User;
import com.cicd.userservice.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void getAllUsersReturnsUsersFromService() {
        List<User> users = Arrays.asList(
                new User("1", "John Doe", "john@example.com"),
                new User("2", "Jane Smith", "jane@example.com")
        );
        when(userService.getAllUsers()).thenReturn(users);

        List<User> result = userController.getAllUsers();

        assertEquals(2, result.size());
        verify(userService).getAllUsers();
    }

    @Test
    void getUserReturnsOkWhenUserExists() {
        User user = new User("1", "John Doe", "john@example.com");
        when(userService.getUserById("1")).thenReturn(Optional.of(user));

        ResponseEntity<User> response = userController.getUser("1");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("1", response.getBody().getId());
        verify(userService).getUserById("1");
    }

    @Test
    void getUserReturnsNotFoundWhenMissing() {
        when(userService.getUserById("404")).thenReturn(Optional.empty());

        ResponseEntity<User> response = userController.getUser("404");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
        verify(userService).getUserById("404");
    }

    @Test
    void createUserReturnsCreated() {
        User input = new User("3", "Alice", "alice@example.com");
        when(userService.createUser(input)).thenReturn(input);

        ResponseEntity<User> response = userController.createUser(input);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("3", response.getBody().getId());
        verify(userService).createUser(input);
    }

    @Test
    void healthReturnsServiceHealth() {
        Map<String, String> expected = Map.of("status", "UP", "service", "user-service");
        when(userService.health()).thenReturn(expected);

        Map<String, String> response = userController.health();

        assertEquals("UP", response.get("status"));
        assertEquals("user-service", response.get("service"));
        verify(userService).health();
    }
}
