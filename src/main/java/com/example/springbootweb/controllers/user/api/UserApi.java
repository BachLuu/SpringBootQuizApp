package com.example.springbootweb.controllers.user.api;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.springbootweb.entities.dtos.users.CreateUserRequest;
import com.example.springbootweb.entities.dtos.users.UpdateUserRequest;
import com.example.springbootweb.entities.dtos.users.UserDetailResponse;
import com.example.springbootweb.entities.dtos.users.UserFilter;
import com.example.springbootweb.entities.dtos.users.UserSummaryResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * API Interface for User operations.
 * Contains all Swagger/OpenAPI documentation annotations.
 * Controller implements this interface to keep code clean.
 */
@Tag(name = "User", description = "User management APIs")
public interface UserApi {

    // ==================== READ Operations ====================

    @Operation(summary = "Get all users", 
               description = "Retrieve a list of all users in the system with optional filtering")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user list"),
        @ApiResponse(responseCode = "204", description = "No users found")
    })
    ResponseEntity<List<UserSummaryResponse>> getAllUsers(
            @ModelAttribute UserFilter filter);

    @Operation(summary = "Get paged users", 
               description = "Retrieve users with pagination and optional filtering support")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved paged users")
    })
    ResponseEntity<Page<UserSummaryResponse>> getPagedUsers(
            @Parameter(description = "Page number (0-based)", example = "0") Integer page,
            @Parameter(description = "Page size", example = "10") Integer size,
            @ModelAttribute UserFilter filter);

    @Operation(summary = "Get user by ID", 
               description = "Retrieve detailed information about a specific user")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user details",
            content = @Content(schema = @Schema(implementation = UserDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<UserDetailResponse> getUserById(
            @Parameter(description = "User ID", required = true) UUID id);

    @Operation(summary = "Get active users", 
               description = "Retrieve all currently active users")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved active users"),
        @ApiResponse(responseCode = "204", description = "No active users found")
    })
    ResponseEntity<List<UserSummaryResponse>> getActiveUsers();

    @Operation(summary = "Search users", 
               description = "Search for users by keyword (name, email, etc.)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved matching users"),
        @ApiResponse(responseCode = "204", description = "No users found matching the search criteria")
    })
    ResponseEntity<List<UserSummaryResponse>> searchUsers(
            @Parameter(description = "Search keyword", required = true) String keyword);

    @Operation(summary = "Get total user count", 
               description = "Get the total number of users in the system")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user count")
    })
    ResponseEntity<Map<String, Long>> getTotalUsers();

    // ==================== WRITE Operations ====================

    @Operation(summary = "Create a new user", 
               description = "Create a new user with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "User created successfully",
            content = @Content(schema = @Schema(implementation = UserDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid user data or email already exists")
    })
    ResponseEntity<UserDetailResponse> createUser(CreateUserRequest createUserRequest);

    @Operation(summary = "Update a user", 
               description = "Update an existing user with the provided details")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(schema = @Schema(implementation = UserDetailResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid user data provided"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<UserDetailResponse> updateUser(
            @Parameter(description = "User ID", required = true) UUID id,
            UpdateUserRequest updateUserRequest);

    @Operation(summary = "Delete a user", 
               description = "Delete a user by their ID")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    ResponseEntity<Void> deleteUser(
            @Parameter(description = "User ID", required = true) UUID id);
}
