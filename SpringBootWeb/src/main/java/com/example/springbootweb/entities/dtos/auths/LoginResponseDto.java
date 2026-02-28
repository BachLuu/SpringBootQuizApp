package com.example.springbootweb.entities.dtos.auths;

import org.jspecify.annotations.NonNull;

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
    @NonNull
    @NotNull
    private String accessToken;

    @NonNull
    @NotNull
    private RefreshToken refreshToken;
}
