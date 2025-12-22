package com.example.springbootweb.entities.dtos.auths;

import com.example.springbootweb.entities.models.RefreshToken;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDto {
    @NotNull
    private String accessToken;
    @NotNull
    private RefreshToken refreshToken;
}

