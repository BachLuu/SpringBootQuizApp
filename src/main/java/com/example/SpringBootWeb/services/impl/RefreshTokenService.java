package com.example.SpringBootWeb.services.impl;

import java.time.Instant;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.SpringBootWeb.entities.models.RefreshToken;
import com.example.SpringBootWeb.entities.models.User;
import com.example.SpringBootWeb.repositories.RefreshTokenRepository;
import com.example.SpringBootWeb.repositories.UserRepository;
import com.example.SpringBootWeb.services.interfaces.IRefreshTokenService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RefreshTokenService implements IRefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final long refreshTtlSeconds = 7L * 24 * 60 * 60;

    @Override
    public RefreshToken saveRefreshToken(String email, RefreshToken refreshToken) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        // Một user chỉ giữ 1 refresh token: xóa cái cũ nếu có
        refreshTokenRepository.deleteByUser(user);

        RefreshToken entity = RefreshToken.builder()
                .token(refreshToken.getToken())
                .user(user)
                .expiryDate(Instant.now().plusSeconds(refreshTtlSeconds))
                .build();

        return refreshTokenRepository.save(entity);
    }

}
