package com.cicd.userservice.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class UserTest {

    @Test
    void defaultConstructorAndSettersPopulateFields() {
        User user = new User();

        assertNull(user.getId());
        assertNull(user.getName());
        assertNull(user.getEmail());

        user.setId("10");
        user.setName("Test Name");
        user.setEmail("test@example.com");

        assertEquals("10", user.getId());
        assertEquals("Test Name", user.getName());
        assertEquals("test@example.com", user.getEmail());
    }

    @Test
    void allArgsConstructorSetsAllFields() {
        User user = new User("11", "Jane", "jane@example.com");

        assertEquals("11", user.getId());
        assertEquals("Jane", user.getName());
        assertEquals("jane@example.com", user.getEmail());
    }
}
