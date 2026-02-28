package com.example.springbootweb.services.interfaces;

import java.util.UUID;

import com.example.springbootweb.entities.dtos.auths.LoginRequestDto;
import com.example.springbootweb.entities.dtos.auths.LoginResponseDto;
import com.example.springbootweb.entities.dtos.auths.RegisterRequestDto;
import com.example.springbootweb.entities.dtos.users.UserDetailResponse;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.data.util.Pair;

public interface IAuthService {
    LoginResponseDto login(LoginRequestDto request, HttpServletResponse response);

    void register(RegisterRequestDto request);

    UserDetailResponse getCurrentUser(String accessToken);

    void logout(HttpServletRequest request, HttpServletResponse response);

    Pair<Boolean, String> refreshToken(HttpServletRequest request, HttpServletResponse response);

    /**
     * Get user ID by email (username from UserDetails)
     * @param email The user's email address
     * @return UUID of the user
     */
    UUID getUserIdByEmail(String email);
}
