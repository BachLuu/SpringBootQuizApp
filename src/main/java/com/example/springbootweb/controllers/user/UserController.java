package com.example.springbootweb.controllers.user;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootweb.controllers.user.api.UserApi;
import com.example.springbootweb.entities.dtos.users.CreateUserRequest;
import com.example.springbootweb.entities.dtos.users.UpdateUserRequest;
import com.example.springbootweb.entities.dtos.users.UserDetailResponse;
import com.example.springbootweb.entities.dtos.users.UserSummaryResponse;
import com.example.springbootweb.services.interfaces.IUserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for User management operations.
 * Implements UserApi interface for clean separation of Swagger documentation.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController implements UserApi {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final IUserService userService;

    // ==================== READ Operations ====================

    @Override
    @GetMapping
    public ResponseEntity<List<UserSummaryResponse>> getAllUsers() {
        log.info("GET /api/users");
        List<UserSummaryResponse> users = userService.getAllUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @Override
    @GetMapping("/paged")
    public ResponseEntity<Page<UserSummaryResponse>> getPagedUsers(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size) {
        log.info("GET /api/users/paged - page: {}, size: {}", page, size);
        return ResponseEntity.ok(userService.getPagedUsers(page, size));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<UserDetailResponse> getUserById(@PathVariable("id") UUID id) {
        log.info("GET /api/users/{}", id);
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @Override
    @GetMapping("/active")
    public ResponseEntity<List<UserSummaryResponse>> getActiveUsers() {
        log.info("GET /api/users/active");
        List<UserSummaryResponse> users = userService.getActiveUsers();
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<List<UserSummaryResponse>> searchUsers(
            @RequestParam("keyword") String keyword) {
        log.info("GET /api/users/search - keyword: {}", keyword);
        List<UserSummaryResponse> users = userService.searchUsers(keyword);
        if (users.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(users);
    }

    @Override
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalUsers() {
        log.info("GET /api/users/count");
        Map<String, Long> response = new HashMap<>();
        response.put("total", userService.getTotalUsers());
        return ResponseEntity.ok(response);
    }

    // ==================== WRITE Operations ====================

    @Override
    @PostMapping
    public ResponseEntity<UserDetailResponse> createUser(
            @Valid @RequestBody CreateUserRequest createUserRequest) {
        log.info("POST /api/users - Creating: {}", createUserRequest.email());
        UserDetailResponse created = userService.createUser(createUserRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<UserDetailResponse> updateUser(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateUserRequest updateUserRequest) {
        log.info("PATCH /api/users/{}", id);
        return ResponseEntity.ok(userService.updateUser(id, updateUserRequest));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id) {
        log.info("DELETE /api/users/{}", id);
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
