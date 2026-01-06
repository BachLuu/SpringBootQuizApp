package com.example.springbootweb.controllers;

import com.example.springbootweb.entities.dtos.roles.CreateRoleRequest;
import com.example.springbootweb.entities.dtos.roles.RoleDetailResponse;
import com.example.springbootweb.entities.dtos.roles.RoleSummaryResponse;
import com.example.springbootweb.entities.dtos.roles.UpdateRoleRequest;
import com.example.springbootweb.services.interfaces.IRoleService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Role management operations
 * Handles all CRUD operations and search functionality for roles
 *
 * @author SpringBootWeb Team
 * @version 1.0
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

  private static final Logger logger = LoggerFactory.getLogger(
    RoleController.class
  );
  private final IRoleService roleService;

  /**
   * Get all roles
   *
   * @return ResponseEntity containing list of all roles
   */
@GetMapping
  public ResponseEntity<List<RoleSummaryResponse>> getAllRoles() {
    logger.info("GET /api/roles - Fetching all roles");

    List<RoleSummaryResponse> roles = roleService.getAllRoles();

    if (roles.isEmpty()) {
      logger.info("No roles found");
      return ResponseEntity.noContent().build();
    }

    logger.info("Successfully retrieved {} roles", roles.size());
    return ResponseEntity.ok(roles);
  }

  @GetMapping("/paged")
  public ResponseEntity<Page<RoleSummaryResponse>> getPagedRoles(
    @RequestParam(name = "page", defaultValue = "0") Integer page,
    @RequestParam(name = "size", defaultValue = "10") Integer size
  ) {
    logger.info(
      "GET /api/roles/paged - Fetching roles with pagination, page: {}, size: {}",
      page,
      size
    );
    Page<RoleSummaryResponse> roles = roleService.getPagedRoles(page, size);
    return ResponseEntity.ok(roles);
  }

  /**
   * Get role by ID
   *
   * @param id Role UUID
   * @return ResponseEntity containing role details
   */
  @GetMapping("/{id}")
  public ResponseEntity<RoleDetailResponse> getRoleById(@PathVariable("id") UUID id) {
    logger.info("GET /api/roles/{} - Fetching role details", id);

    RoleDetailResponse role = roleService.getRoleById(id);

    logger.info("Successfully retrieved role: {}", role.name());
    return ResponseEntity.ok(role);
  }

  /**
   * Get all active roles
   *
   * @return ResponseEntity containing list of active roles
   */
  @GetMapping("/active")
  public ResponseEntity<List<RoleSummaryResponse>> getActiveRoles() {
    logger.info("GET /api/roles/active - Fetching active roles");

    List<RoleSummaryResponse> roles = roleService.getActiveRoles();

    if (roles.isEmpty()) {
      logger.info("No active roles found");
      return ResponseEntity.noContent().build();
    }

    logger.info("Successfully retrieved {} active roles", roles.size());
    return ResponseEntity.ok(roles);
  }

  /**
   * Search roles by name
   *
   * @param name Search keyword
   * @return ResponseEntity containing list of matching roles
   */
  @GetMapping("/search")
  public ResponseEntity<List<RoleSummaryResponse>> searchByName(
    @RequestParam("name") String name
  ) {
    logger.info("GET /api/roles/search - Searching roles with name: {}", name);

    List<RoleSummaryResponse> roles = roleService.searchByName(name);

    if (roles.isEmpty()) {
      logger.info("No roles found matching name: {}", name);
      return ResponseEntity.noContent().build();
    }

    logger.info("Found {} roles matching name: {}", roles.size(), name);
    return ResponseEntity.ok(roles);
  }

  /**
   * Create a new role
   *
   * @param createRoleDto Role creation data
   * @return ResponseEntity containing created role
   */
  @PostMapping
  public ResponseEntity<RoleDetailResponse> createRole(
    @Valid @RequestBody CreateRoleRequest createRoleRequest
  ) {
    logger.info(
      "POST /api/roles - Creating new role: {}",
      createRoleRequest.name()
    );

    RoleDetailResponse createdRole = roleService.createRole(createRoleRequest);

    logger.info("Successfully created role with id: {}", createdRole.id());
    return ResponseEntity.status(HttpStatus.CREATED).body(createdRole);
  }

  /**
   * Update an existing role
   *
   * @param id        Role UUID
   * @param updateDto Role update data
   * @return ResponseEntity containing updated role
   */
  @PutMapping("/{id}")
  public ResponseEntity<RoleDetailResponse> updateRole(
    @PathVariable("id") UUID id,
    @Valid @RequestBody UpdateRoleRequest updateDto
  ) {
    logger.info("PUT /api/roles/{} - Updating role", id);

    RoleDetailResponse updatedRole = roleService.updateRole(id, updateDto);

    logger.info("Successfully updated role with id: {}", updatedRole.id());
    return ResponseEntity.ok(updatedRole);
  }

  /**
   * Delete a role
   *
   * @param id Role UUID
   * @return ResponseEntity with no content
   */
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRole(@PathVariable("id") UUID id) {
    logger.info("DELETE /api/roles/{} - Deleting role", id);

    roleService.deleteRole(id);

    logger.info("Successfully deleted role with id: {}", id);
    return ResponseEntity.noContent().build();
  }

  /**
   * Get total count of roles
   *
   * @return ResponseEntity containing total role count
   */
  @GetMapping("/count")
  public ResponseEntity<Map<String, Long>> getTotalRoles() {
    logger.info("GET /api/roles/count - Fetching total role count");

    long count = roleService.getTotalRoles();

    Map<String, Long> response = new HashMap<>();
    response.put("total", count);

    logger.info("Total roles: {}", count);
    return ResponseEntity.ok(response);
  }
}
