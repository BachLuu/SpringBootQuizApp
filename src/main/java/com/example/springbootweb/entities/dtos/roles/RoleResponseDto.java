package com.example.springbootweb.entities.dtos.roles;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleResponseDto {
    private UUID id;
    private String name;
    private String description;
    private Boolean isActive;
}
