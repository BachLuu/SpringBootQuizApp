package com.example.springbootweb.controllers.role;

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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootweb.controllers.role.api.RoleApi;
import com.example.springbootweb.entities.dtos.roles.CreateRoleRequest;
import com.example.springbootweb.entities.dtos.roles.RoleDetailResponse;
import com.example.springbootweb.entities.dtos.roles.RoleFilter;
import com.example.springbootweb.entities.dtos.roles.RoleSummaryResponse;
import com.example.springbootweb.entities.dtos.roles.UpdateRoleRequest;
import com.example.springbootweb.services.interfaces.IRoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Role management operations.
 * Implements RoleApi interface for clean separation of Swagger documentation.
 */
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController implements RoleApi {

    private static final Logger log = LoggerFactory.getLogger(RoleController.class);
    private final IRoleService roleService;

    // ==================== READ Operations ====================

    @Override
    @GetMapping
    public ResponseEntity<List<RoleSummaryResponse>> getAllRoles(
            @ModelAttribute RoleFilter filter) {
        log.info("GET /api/roles with filter: {}", filter);
        List<RoleSummaryResponse> roles = roleService.getAllRoles(filter);
        if (roles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles);
    }

    @Override
    @GetMapping("/paged")
    public ResponseEntity<Page<RoleSummaryResponse>> getPagedRoles(
            @RequestParam(name = "page", defaultValue = "0") Integer page,
            @RequestParam(name = "size", defaultValue = "10") Integer size,
            @ModelAttribute RoleFilter filter) {
        log.info("GET /api/roles/paged - page: {}, size: {}, filter: {}", page, size, filter);
        return ResponseEntity.ok(roleService.getPagedRoles(page, size, filter));
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<RoleDetailResponse> getRoleById(@PathVariable("id") UUID id) {
        log.info("GET /api/roles/{}", id);
        return ResponseEntity.ok(roleService.getRoleById(id));
    }

    @Override
    @GetMapping("/active")
    public ResponseEntity<List<RoleSummaryResponse>> getActiveRoles() {
        log.info("GET /api/roles/active");
        List<RoleSummaryResponse> roles = roleService.getActiveRoles();
        if (roles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles);
    }

    @Override
    @GetMapping("/search")
    public ResponseEntity<List<RoleSummaryResponse>> searchByName(
            @RequestParam("name") String name) {
        log.info("GET /api/roles/search - name: {}", name);
        List<RoleSummaryResponse> roles = roleService.searchByName(name);
        if (roles.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(roles);
    }

    @Override
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getTotalRoles() {
        log.info("GET /api/roles/count");
        Map<String, Long> response = new HashMap<>();
        response.put("total", roleService.getTotalRoles());
        return ResponseEntity.ok(response);
    }

    // ==================== WRITE Operations ====================

    @Override
    @PostMapping
    public ResponseEntity<RoleDetailResponse> createRole(
            @Valid @RequestBody CreateRoleRequest createRoleRequest) {
        log.info("POST /api/roles - Creating: {}", createRoleRequest.name());
        RoleDetailResponse created = roleService.createRole(createRoleRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Override
    @PutMapping("/{id}")
    public ResponseEntity<RoleDetailResponse> updateRole(
            @PathVariable("id") UUID id,
            @Valid @RequestBody UpdateRoleRequest updateDto) {
        log.info("PUT /api/roles/{}", id);
        return ResponseEntity.ok(roleService.updateRole(id, updateDto));
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable("id") UUID id) {
        log.info("DELETE /api/roles/{}", id);
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}
