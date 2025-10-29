package com.example.SpringBootWeb.services.interfaces;

import com.example.SpringBootWeb.dtos.LoginRequestDto;
import com.example.SpringBootWeb.dtos.LoginResponseDto;
import com.example.SpringBootWeb.dtos.RegisterRequestDto;

public interface IAuthService {
    LoginResponseDto login(LoginRequestDto request);
    void register(RegisterRequestDto request);
}

