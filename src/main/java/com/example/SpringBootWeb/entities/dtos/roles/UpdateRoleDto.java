package com.example.SpringBootWeb.entities.dtos.roles;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateRoleDto {
    @Size(min = 3, max = 50, message = "Role name must be between 3 and 50 characters")
    private String name;

    @Size(min = 3, max = 50, message = "Description must be between 3 and 50 characters")
    private String description;

    private Boolean isActive;
}
