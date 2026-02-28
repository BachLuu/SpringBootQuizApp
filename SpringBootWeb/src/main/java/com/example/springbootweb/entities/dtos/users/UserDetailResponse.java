package com.example.springbootweb.entities.dtos.users;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import com.example.springbootweb.entities.dtos.roles.RoleSummaryResponse;

public record UserDetailResponse(UUID id, String firstName, String lastName, String email, String avatar,
		LocalDate dateOfBirth, Boolean isActive, LocalDateTime createdAt, Set<RoleSummaryResponse> roles,
		String displayName) {
}
