package com.example.SpringBootWeb.services.interfaces;

import com.example.SpringBootWeb.entities.dtos.roles.CreateRoleDto;
import com.example.SpringBootWeb.entities.dtos.roles.RoleResponseDto;
import com.example.SpringBootWeb.entities.dtos.roles.UpdateRoleDto;

import java.util.List;
import java.util.UUID;

public interface IRoleService {
    List<RoleResponseDto> getAllRoles();

    RoleResponseDto getRoleById(UUID id);

    List<RoleResponseDto> getActiveRoles();

    List<RoleResponseDto> searchByName(String name);

    RoleResponseDto createRole(CreateRoleDto createRoleDto);

    RoleResponseDto updateRole(UUID id, UpdateRoleDto updateRoleDto);

    void deleteRole(UUID id);

    long getTotalRoles();
}
