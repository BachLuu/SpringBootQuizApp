package com.example.springbootweb.entities.dtos.roles;

import java.util.UUID;

public record RoleSummaryResponse(UUID id, String name, Boolean isActive) {
}
