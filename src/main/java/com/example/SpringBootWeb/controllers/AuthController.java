package com.example.SpringBootWeb.controllers;

import com.example.SpringBootWeb.dtos.LoginRequestDto;
import com.example.SpringBootWeb.dtos.LoginResponseDto;
import com.example.SpringBootWeb.dtos.RegisterRequestDto;
import com.example.SpringBootWeb.services.interfaces.IAuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final IAuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto request) {
        LoginResponseDto token = authService.login(request);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody RegisterRequestDto request) {
        authService.register(request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request) {
        // In a stateless JWT implementation, the client is responsible for deleting the token.
        // The server can't really "log out" a user without maintaining state.
        // A blacklist/revocation strategy could be implemented if needed.
        return ResponseEntity.ok().build();
    }
}

