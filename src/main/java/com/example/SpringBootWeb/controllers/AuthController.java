package com.example.SpringBootWeb.controllers;

import com.example.SpringBootWeb.entities.constants.SuccessMessage;
import com.example.SpringBootWeb.entities.dtos.auths.LoginRequestDto;
import com.example.SpringBootWeb.entities.dtos.auths.LoginResponseDto;
import com.example.SpringBootWeb.entities.dtos.auths.RegisterRequestDto;
import com.example.SpringBootWeb.entities.dtos.users.UserResponseDto;
import com.example.SpringBootWeb.services.interfaces.IAuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;
    private static final Logger LOG = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {
        LoginResponseDto token = authService.login(request, response);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok().body(Map.of("message", SuccessMessage.REGISTER_SUCCESS));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser(HttpServletRequest request) {
        String token = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("access-token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else {
            UserResponseDto currentUser = authService.getCurrentUser(token);
            return ResponseEntity.ok(currentUser);
        }
    }

    @GetMapping("/refresh-token")
    public ResponseEntity<Boolean> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        Pair<Boolean, String> refreshTokenResult = authService.refreshToken(request, response);
        if (refreshTokenResult.getFirst()) {
            LOG.info(refreshTokenResult.getSecond());
            return ResponseEntity.ok(true);
        }
        LOG.info(refreshTokenResult.getSecond());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(false);
    }
}
