package com.example.springbootweb.controllers.auth;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.springbootweb.controllers.auth.api.AuthApi;
import com.example.springbootweb.entities.constants.SuccessMessage;
import com.example.springbootweb.entities.dtos.auths.LoginRequestDto;
import com.example.springbootweb.entities.dtos.auths.LoginResponseDto;
import com.example.springbootweb.entities.dtos.auths.RegisterRequestDto;
import com.example.springbootweb.entities.dtos.users.UserDetailResponse;
import com.example.springbootweb.services.interfaces.IAuthService;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

/**
 * REST Controller for Authentication operations.
 * Implements AuthApi interface for clean separation of Swagger documentation.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApi {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    private final IAuthService authService;

    @Override
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto request,
            HttpServletResponse response) {
        log.info("POST /api/auth/login - email: {}", request.getEmail());
        LoginResponseDto token = authService.login(request, response);
        return ResponseEntity.ok(token);
    }

    @Override
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequestDto request) {
        log.info("POST /api/auth/register - email: {}", request.getEmail());
        authService.register(request);
        return ResponseEntity.ok().body(Map.of("message", SuccessMessage.REGISTER_SUCCESS));
    }

    @Override
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        log.info("POST /api/auth/logout");
        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @Override
    @GetMapping("/me")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<UserDetailResponse> getCurrentUser(HttpServletRequest request) {
        log.info("GET /api/auth/me");
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access_token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok(authService.getCurrentUser(token));
    }

    @Override
    @GetMapping("/refresh-token")
    public ResponseEntity<Boolean> refreshToken(
            HttpServletRequest request,
            HttpServletResponse response) {
        log.debug("GET /api/auth/refresh-token");
        Pair<Boolean, String> result = authService.refreshToken(request, response);
        if (result.getFirst().booleanValue()) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }
}
