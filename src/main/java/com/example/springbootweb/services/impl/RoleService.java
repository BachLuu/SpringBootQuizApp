package com.example.springbootweb.services.impl;

import com.example.springbootweb.entities.constants.ErrorMessage;
import com.example.springbootweb.entities.dtos.roles.CreateRoleRequest;
import com.example.springbootweb.entities.dtos.roles.RoleDetailResponse;
import com.example.springbootweb.entities.dtos.roles.RoleSummaryResponse;
import com.example.springbootweb.entities.dtos.roles.UpdateRoleRequest;
import com.example.springbootweb.entities.models.Role;
import com.example.springbootweb.exceptions.BadRequestException;
import com.example.springbootweb.exceptions.ResourceNotFoundException;
import com.example.springbootweb.mappers.RoleMapper;
import com.example.springbootweb.repositories.RoleRepository;
import com.example.springbootweb.services.interfaces.IRoleService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

	private final RoleMapper roleMapper;

	@Override
	@Transactional(readOnly = true)
	public List<RoleSummaryResponse> getAllRoles() {
		logger.info("Fetching all roles");
		List<Role> roles = roleRepository.findAll();
		return roles.stream().map(roleMapper::toSummary).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public Page<RoleSummaryResponse> getPagedRoles(Integer page, Integer size) {
		int pageNumber = page == null ? 0 : page;
		int pageSize = size == null ? 10 : size;

		if (pageNumber < 0) {
			throw new BadRequestException("Page must be >= 0");
		}
		if (pageSize < 1 || pageSize > 100) {
			throw new BadRequestException("Size must be between 1 and 100");
		}

		Page<Role> rolePage = roleRepository.findAll(PageRequest.of(pageNumber, pageSize));
		return rolePage.map(roleMapper::toSummary);
	}

	@Override
	@Transactional(readOnly = true)
	public RoleDetailResponse getRoleById(UUID id) {
		logger.info("Fetching role with id: {}", id);
		Role role = roleRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));
		return roleMapper.toResponse(role);
	}

	@Override
	@Transactional(readOnly = true)
	public List<RoleSummaryResponse> getActiveRoles() {
		logger.info("Fetching active roles");
		List<Role> roles = roleRepository.findByIsActiveTrue();
		return roles.stream().map(roleMapper::toSummary).collect(Collectors.toList());
	}

	@Override
	@Transactional(readOnly = true)
	public List<RoleSummaryResponse> searchByName(String name) {
		logger.info("Searching roles with name containing: {}", name);
		if (name == null || name.trim().isEmpty()) {
			throw new BadRequestException("Search name cannot be empty");
		}
		List<Role> roles = roleRepository.findByNameContainingIgnoreCase(name);
		return roles.stream().map(roleMapper::toSummary).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public RoleDetailResponse createRole(CreateRoleRequest createRoleRequest) {
		logger.info("Creating new role: {}", createRoleRequest.name());
		if (roleRepository.existsByName(createRoleRequest.name())) {
			throw new BadRequestException("Role with name " + createRoleRequest.name() + " already exists");
		}

		Role role = roleMapper.toEntity(createRoleRequest);

		Role savedRole = roleRepository.save(role);
		return roleMapper.toResponse(savedRole);
	}

	@Override
	@Transactional
	public RoleDetailResponse updateRole(UUID id, UpdateRoleRequest updateRoleRequest) {
		logger.info("Updating role with id: {}", id);
		Role role = roleRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + id));

		if (updateRoleRequest.name() != null && !updateRoleRequest.name().equals(role.getName())) {
			if (roleRepository.existsByName(updateRoleRequest.name())) {
				throw new BadRequestException("Role with name " + updateRoleRequest.name() + " already exists");
			}
		}

		roleMapper.updateEntity(updateRoleRequest, role);

		Role updatedRole = roleRepository.save(role);
		return roleMapper.toResponse(updatedRole);
	}

	@Override
	@Transactional
	public void deleteRole(UUID id) {
		logger.info("Deleting role with id: {}", id);
		if (!roleRepository.existsById(id)) {
			throw new ResourceNotFoundException("Role not found with id: " + id);
		}
		Role role = roleRepository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.ROLE_NOT_FOUND + id));
		role.setIsActive(false);
		roleRepository.save(role);
	}

	public long getTotalRoles() {
		return roleRepository.count();
	}

}
