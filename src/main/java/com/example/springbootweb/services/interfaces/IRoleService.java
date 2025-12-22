package com.example.springbootweb.services.interfaces;

import com.example.springbootweb.entities.dtos.roles.CreateRoleDto;
import com.example.springbootweb.entities.dtos.roles.RoleResponseDto;
import com.example.springbootweb.entities.dtos.roles.UpdateRoleDto;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IRoleService {
    List<RoleResponseDto> getAllRoles();

    Page<RoleResponseDto> getPagedRoles(Integer page, Integer size);

    RoleResponseDto getRoleById(UUID id);

    List<RoleResponseDto> getActiveRoles();

    List<RoleResponseDto> searchByName(String name);

    RoleResponseDto createRole(CreateRoleDto createRoleDto);

    RoleResponseDto updateRole(UUID id, UpdateRoleDto updateRoleDto);

    void deleteRole(UUID id);

    long getTotalRoles();
}
