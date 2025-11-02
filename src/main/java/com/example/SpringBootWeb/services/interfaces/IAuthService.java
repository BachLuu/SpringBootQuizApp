package com.example.SpringBootWeb.services.interfaces;

import com.example.SpringBootWeb.dtos.auths.LoginRequestDto;
import com.example.SpringBootWeb.dtos.auths.LoginResponseDto;
import com.example.SpringBootWeb.dtos.auths.RegisterRequestDto;
import com.example.SpringBootWeb.dtos.users.UserResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IAuthService {
    LoginResponseDto login(LoginRequestDto request, HttpServletResponse response);

    void register(RegisterRequestDto request);

    UserResponseDto getCurrentUser(String accessToken);

    void logout(HttpServletRequest request, HttpServletResponse response);
}
