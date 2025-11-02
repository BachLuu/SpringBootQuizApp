package com.example.SpringBootWeb.dtos.users;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * DTO for {@link com.example.SpringBootWeb.entities.User}
 */
@Data
@Builder
public class UserResponseDto implements Serializable {
    UUID id;
    @NotNull
    @Size(min = 3, max = 50)
    String firstName;
    @NotNull
    @Size(min = 3, max = 50)
    String lastName;
    @NotNull
    @Email
    String email;
    @Size(max = 500)
    String avatar;
    LocalDate dateOfBirth;
    Boolean isActive;
    LocalDateTime createdAt;
    Set<RoleDto> roles;
    String displayName;

    @Data
    @Builder
    public static class RoleDto implements Serializable {
        String name;
        Boolean isActive;
    }
}