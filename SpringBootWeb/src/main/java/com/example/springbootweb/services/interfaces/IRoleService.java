package com.example.springbootweb.services.interfaces;

import com.example.springbootweb.entities.dtos.roles.CreateRoleRequest;
import com.example.springbootweb.entities.dtos.roles.RoleDetailResponse;
import com.example.springbootweb.entities.dtos.roles.RoleFilter;
import com.example.springbootweb.entities.dtos.roles.RoleSummaryResponse;
import com.example.springbootweb.entities.dtos.roles.UpdateRoleRequest;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

public interface IRoleService {
    List<RoleSummaryResponse> getAllRoles(RoleFilter filter);

    Page<RoleSummaryResponse> getPagedRoles(Integer page, Integer size, RoleFilter filter);

    RoleDetailResponse getRoleById(UUID id);

    List<RoleSummaryResponse> getActiveRoles();

    List<RoleSummaryResponse> searchByName(String name);

    RoleDetailResponse createRole(CreateRoleRequest createRoleRequest);

    RoleDetailResponse updateRole(UUID id, UpdateRoleRequest updateRoleRequest);

    void deleteRole(UUID id);

    long getTotalRoles();
}
