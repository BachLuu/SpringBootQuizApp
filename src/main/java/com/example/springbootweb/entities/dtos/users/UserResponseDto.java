package com.example.springbootweb.entities.dtos.users;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import org.jspecify.annotations.NonNull;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for {@link com.example.springbootweb.entities.User}
 */
@Data
@Builder
public class UserResponseDto implements Serializable {
    private UUID id;

    @NonNull
    @NotNull
    @Size(min = 3, max = 50)
    private String firstName;

    @NonNull
    @NotNull
    @Size(min = 3, max = 50)
    private String lastName;

    @NonNull
    @NotNull
    @Email
    private String email;
    @Size(max = 500)
    private String avatar;
    private LocalDate dateOfBirth;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private Set<RoleDto> roles;
    private String displayName;

    @Data
    @Builder
    public static class RoleDto implements Serializable {
        private String name;
        private Boolean isActive;
    }
}