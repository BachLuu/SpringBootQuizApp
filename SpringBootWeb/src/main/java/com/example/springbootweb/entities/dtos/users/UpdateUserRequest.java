package com.example.springbootweb.entities.dtos.users;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.Size;

public record UpdateUserRequest(
        @Size(min = 3, max = 50, message = "First name must be between 3 and 50 characters") String firstName,

        @Size(min = 3, max = 50, message = "Last name must be between 3 and 50 characters") String lastName,

        String avatar,
        LocalDate dateOfBirth,
        Boolean isActive,
        Set<UUID> roleIds) {
}
