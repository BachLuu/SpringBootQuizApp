package com.example.springbootweb.controllers.role.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;

import com.example.springbootweb.entities.dtos.roles.CreateRoleRequest;
import com.example.springbootweb.entities.dtos.roles.RoleDetailResponse;
import com.example.springbootweb.entities.dtos.roles.RoleSummaryResponse;
import com.example.springbootweb.entities.dtos.roles.UpdateRoleRequest;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Interface for Role operations.
 * Contains all Swagger/OpenAPI documentation annotations.
 * Controller implements this interface to keep code clean.
 */
@Tag(name = "Role", description = "Role management APIs")
public interface RoleApi {

    // ==================== READ Operations ====================

    @Operation(summary = "Get all roles", 
               description = "Retrieve a list of all roles in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved role list"),
        @ApiResponse(responseCode = "204", description = "No roles found")
    })
    ResponseEntity<List<RoleSummaryResponse>> getAllRoles();

    @Operation(summary = "Get paged roles", 
               description = "Retrieve roles with pagination support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paged roles")
    })
    ResponseEntity<Page<RoleSummaryResponse>> getPagedRoles(
            @Parameter(description = "Page number (0-based)", example = "0") Integer page,
            @Parameter(description = "Page size", example = "10") Integer size);

    @Operation(summary = "Get role by ID", 
               description = "Retrieve detailed information about a specific role")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved role details",
            content = @Content(schema = @Schema(implementation = RoleDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    ResponseEntity<RoleDetailResponse> getRoleById(
            @Parameter(description = "Role ID", required = true) UUID id);

    @Operation(summary = "Get active roles", 
               description = "Retrieve all currently active roles")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active roles"),
        @ApiResponse(responseCode = "204", description = "No active roles found")
    })
    ResponseEntity<List<RoleSummaryResponse>> getActiveRoles();

    @Operation(summary = "Search roles by name", 
               description = "Search for roles containing the specified name keyword")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching roles"),
        @ApiResponse(responseCode = "204", description = "No roles found matching the search criteria")
    })
    ResponseEntity<List<RoleSummaryResponse>> searchByName(
            @Parameter(description = "Search keyword for role name", required = true) String name);

    @Operation(summary = "Get total role count", 
               description = "Get the total number of roles in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved role count")
    })
    ResponseEntity<Map<String, Long>> getTotalRoles();

    // ==================== WRITE Operations ====================

    @Operation(summary = "Create a new role", 
               description = "Create a new role with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Role created successfully",
            content = @Content(schema = @Schema(implementation = RoleDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid role data or role name already exists")
    })
    ResponseEntity<RoleDetailResponse> createRole(CreateRoleRequest createRoleRequest);

    @Operation(summary = "Update a role", 
               description = "Update an existing role with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Role updated successfully",
            content = @Content(schema = @Schema(implementation = RoleDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid role data provided"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    ResponseEntity<RoleDetailResponse> updateRole(
            @Parameter(description = "Role ID", required = true) UUID id,
            UpdateRoleRequest updateDto);

    @Operation(summary = "Delete a role", 
               description = "Delete a role by its ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    ResponseEntity<Void> deleteRole(
            @Parameter(description = "Role ID", required = true) UUID id);
}
