package com.example.springbootweb.entities.dtos.users;

import java.util.Set;
import java.util.UUID;

public record UserSummaryResponse(UUID id, String firstName, String lastName, String email, Boolean isActive,
		String displayName, Set<String> roles) {
}
