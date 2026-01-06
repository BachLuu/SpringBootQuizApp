package com.example.springbootweb.services.interfaces;

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
}
