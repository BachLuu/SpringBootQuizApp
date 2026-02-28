package com.example.springbootweb.entities.dtos.roles;

import jakarta.validation.constraints.Size;

public record UpdateRoleRequest(
        @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters") String name,

        @Size(min = 3, max = 50, message = "Description must be between 3 and 50 characters") String description,

        Boolean isActive) {
}
