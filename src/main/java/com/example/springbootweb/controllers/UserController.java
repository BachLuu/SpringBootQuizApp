package com.example.springbootweb.controllers;

import com.example.springbootweb.entities.dtos.users.CreateUserRequest;
import com.example.springbootweb.entities.dtos.users.UpdateUserRequest;
import com.example.springbootweb.entities.dtos.users.UserDetailResponse;
import com.example.springbootweb.entities.dtos.users.UserSummaryResponse;
import com.example.springbootweb.services.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	private final IUserService userService;

	@GetMapping
	public ResponseEntity<List<UserSummaryResponse>> getAllUsers() {
		logger.info("GET /api/users - Fetching all users");
		List<UserSummaryResponse> users = userService.getAllUsers();
		if (users.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(users);
	}

	@GetMapping("/paged")
	public ResponseEntity<Page<UserSummaryResponse>> getPagedUsers(
			@RequestParam(name = "page", defaultValue = "0") Integer page,
			@RequestParam(name = "size", defaultValue = "10") Integer size) {
		logger.info("GET /api/users/paged - Fetching users with pagination, page: {}, size: {}", page, size);
		Page<UserSummaryResponse> users = userService.getPagedUsers(page, size);
		return ResponseEntity.ok(users);
	}

	@GetMapping("/{id}")
	public ResponseEntity<UserDetailResponse> getUserById(@PathVariable("id") UUID id) {
		logger.info("GET /api/users/{} - Fetching user details", id);
		UserDetailResponse user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}

	@GetMapping("/active")
	public ResponseEntity<List<UserSummaryResponse>> getActiveUsers() {
		logger.info("GET /api/users/active - Fetching active users");
		List<UserSummaryResponse> users = userService.getActiveUsers();
		if (users.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(users);
	}

	@GetMapping("/search")
	public ResponseEntity<List<UserSummaryResponse>> searchUsers(@RequestParam("keyword") String keyword) {
		logger.info("GET /api/users/search - Searching users with keyword: {}", keyword);
		List<UserSummaryResponse> users = userService.searchUsers(keyword);
		if (users.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(users);
	}

	@PostMapping
	public ResponseEntity<UserDetailResponse> createUser(@Valid @RequestBody CreateUserRequest createUserRequest) {
		logger.info("POST /api/users - Creating new user: {}", createUserRequest.email());
		UserDetailResponse createdUser = userService.createUser(createUserRequest);
		return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
	}

	@PutMapping("/{id}")
	public ResponseEntity<UserDetailResponse> updateUser(@PathVariable("id") UUID id,
			@Valid @RequestBody UpdateUserRequest updateUserRequest) {
		logger.info("PUT /api/users/{} - Updating user", id);
		UserDetailResponse updatedUser = userService.updateUser(id, updateUserRequest);
		return ResponseEntity.ok(updatedUser);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable("id") UUID id) {
		logger.info("DELETE /api/users/{} - Deleting user", id);
		userService.deleteUser(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/count")
	public ResponseEntity<Map<String, Long>> getTotalUsers() {
		logger.info("GET /api/users/count - Fetching total user count");
		long count = userService.getTotalUsers();
		Map<String, Long> response = new HashMap<>();
		response.put("total", count);
		return ResponseEntity.ok(response);
	}

}
