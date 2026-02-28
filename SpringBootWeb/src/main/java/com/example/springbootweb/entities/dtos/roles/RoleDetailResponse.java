package com.example.springbootweb.entities.dtos.roles;

import java.util.UUID;

public record RoleDetailResponse(UUID id, String name, String description, Boolean isActive) {
}
