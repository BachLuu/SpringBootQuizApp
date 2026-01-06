package com.example.springbootweb.entities.dtos.users;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

public record CreateUserRequest(
		@NotBlank(message = "First name is required") @Size(min = 3, max = 50,
				message = "First name must be between 3 and 50 characters") String firstName,

		@NotBlank(message = "Last name is required") @Size(min = 3, max = 50,
				message = "Last name must be between 3 and 50 characters") String lastName,

		@NotBlank(message = "Email is required") @Email(message = "Invalid email format") String email,

		@NotBlank(message = "Password is required") @Size(min = 6,
				message = "Password must be at least 6 characters") String password,

		String avatar, LocalDate dateOfBirth, Set<UUID> roleIds) {
}
