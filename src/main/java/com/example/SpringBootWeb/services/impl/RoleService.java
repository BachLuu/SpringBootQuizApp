package com.example.SpringBootWeb.services.impl;

import com.example.SpringBootWeb.entities.dtos.roles.CreateRoleDto;
import com.example.SpringBootWeb.entities.dtos.roles.RoleResponseDto;
import com.example.SpringBootWeb.entities.dtos.roles.UpdateRoleDto;
import com.example.SpringBootWeb.entities.models.Role;
import com.example.SpringBootWeb.exceptions.BadRequestException;
import com.example.SpringBootWeb.exceptions.ResourceNotFoundException;
import com.example.SpringBootWeb.repositories.RoleRepository;
import com.example.SpringBootWeb.services.interfaces.IRoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);
    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDto> getAllRoles() {
        logger.info("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        return roles.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponseDto getRoleById(UUID id) {
        logger.info("Fetching role with id: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
        return mapToResponseDto(role);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDto> getActiveRoles() {
        logger.info("Fetching active roles");
        List<Role> roles = roleRepository.findByIsActiveTrue();
        return roles.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseDto> searchByName(String name) {
        logger.info("Searching roles with name containing: {}", name);
        if (name == null || name.trim().isEmpty()) {
            throw new BadRequestException("Search name cannot be empty");
        }
        List<Role> roles = roleRepository.findByNameContainingIgnoreCase(name);
        return roles.stream()
                .map(this::mapToResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RoleResponseDto createRole(CreateRoleDto createRoleDto) {
        logger.info("Creating new role: {}", createRoleDto.getName());
        if (roleRepository.existsByName(createRoleDto.getName())) {
            throw new BadRequestException("Role with name " + createRoleDto.getName() + " already exists");
        }

        Role role = Role.builder()
                .name(createRoleDto.getName())
                .description(createRoleDto.getDescription())
                .isActive(true)
                .build();

        Role savedRole = roleRepository.save(role);
        return mapToResponseDto(savedRole);
    }

    @Override
    @Transactional
    public RoleResponseDto updateRole(UUID id, UpdateRoleDto updateRoleDto) {
        logger.info("Updating role with id: {}", id);
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

        if (updateRoleDto.getName() != null && !updateRoleDto.getName().equals(role.getName())) {
            if (roleRepository.existsByName(updateRoleDto.getName())) {
                throw new BadRequestException("Role with name " + updateRoleDto.getName() + " already exists");
            }
            role.setName(updateRoleDto.getName());
        }

        if (updateRoleDto.getDescription() != null) {
            role.setDescription(updateRoleDto.getDescription());
        }

        if (updateRoleDto.getIsActive() != null) {
            role.setIsActive(updateRoleDto.getIsActive());
        }

        Role updatedRole = roleRepository.save(role);
        return mapToResponseDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(UUID id) {
        logger.info("Deleting role with id: {}", id);
        if (!roleRepository.existsById(id)) {
            throw new ResourceNotFoundException("Role not found with id: " + id);
        }
        roleRepository.deleteById(id);
    }

    public long getTotalRoles() {
        return roleRepository.count();
    }

    private RoleResponseDto mapToResponseDto(Role role) {
        return RoleResponseDto.builder()
                .id(role.getId())
                .name(role.getName())
                .description(role.getDescription())
                .isActive(role.getIsActive())
                .build();
    }
}
