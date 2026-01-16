package com.example.springbootweb.controllers.auth.api;

import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.example.springbootweb.entities.dtos.auths.LoginRequestDto;
import com.example.springbootweb.entities.dtos.auths.LoginResponseDto;
import com.example.springbootweb.entities.dtos.auths.RegisterRequestDto;
import com.example.springbootweb.entities.dtos.users.UserDetailResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * API Interface for Authentication operations.
 * Contains all Swagger/OpenAPI documentation annotations.
 * Controller implements this interface to keep code clean.
 */
@Tag(name = "Authentication", description = "Authentication and authorization APIs")
public interface AuthApi {

    @Operation(summary = "User login", 
               description = "Authenticate user with email and password. Returns JWT tokens in response body and sets HTTP-only cookies.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Login successful",
            content = @Content(schema = @Schema(implementation = LoginResponseDto.class))),
        @ApiResponse(responseCode = "401", description = "Invalid credentials")
    })
    ResponseEntity<LoginResponseDto> login(LoginRequestDto request, HttpServletResponse response);

    @Operation(summary = "User registration", 
               description = "Register a new user account")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Registration successful"),
        @ApiResponse(responseCode = "400", description = "Invalid registration data or email already exists")
    })
    ResponseEntity<Map<String, String>> register(RegisterRequestDto request);

    @Operation(summary = "User logout", 
               description = "Logout current user and invalidate tokens")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logout successful")
    })
    ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response);

    @Operation(summary = "Get current user", 
               description = "Get details of the currently authenticated user (Admin only)")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Successfully retrieved user details",
            content = @Content(schema = @Schema(implementation = UserDetailResponse.class))),
        @ApiResponse(responseCode = "401", description = "Not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    ResponseEntity<UserDetailResponse> getCurrentUser(HttpServletRequest request);

    @Operation(summary = "Refresh access token", 
               description = "Refresh the access token using the refresh token from cookies")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Token refreshed successfully"),
        @ApiResponse(responseCode = "401", description = "Invalid or expired refresh token")
    })
    ResponseEntity<Boolean> refreshToken(HttpServletRequest request, HttpServletResponse response);
}
