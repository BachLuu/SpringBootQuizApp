package com.example.SpringBootWeb.services.impl;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.SpringBootWeb.entities.constants.ErrorMessage;
import com.example.SpringBootWeb.entities.constants.SuccessMessage;
import com.example.SpringBootWeb.entities.models.RefreshToken;
import com.example.SpringBootWeb.entities.models.User;
import com.example.SpringBootWeb.repositories.RefreshTokenRepository;
import com.example.SpringBootWeb.repositories.UserRepository;
import com.example.SpringBootWeb.services.interfaces.IRefreshTokenService;

import lombok.RequiredArgsConstructor;

/**
 * @param refreshTokenRepository
 * @param userRepository
 * @param jwtTokenUtil
 * @param customUserDetailsService
 */
@RequiredArgsConstructor
@Service
public class RefreshTokenService implements IRefreshTokenService {
        private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);
        private final RefreshTokenRepository refreshTokenRepository;
        private final UserRepository userRepository;

        @Override
        @Transactional
        public RefreshToken saveRefreshToken(String email, RefreshToken refreshToken) {
                final User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND + email));
                refreshTokenRepository.deleteByUser(user);
                refreshTokenRepository.flush();
                return refreshTokenRepository.save(refreshToken);
        }

        @Override
        public Pair<Boolean, String> validateRefreshToken(String token) {
                RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElse(null);

                return refreshToken == null ? Pair.of(false, ErrorMessage.INVALID_REFRESH_TOKEN)
                                : refreshToken.getExpiryDate().isBefore(Instant.now())
                                                ? Pair.of(false, ErrorMessage.REFRESH_TOKEN_EXPIRED)
                                                : Pair.of(true, SuccessMessage.REFRESH_TOKEN_VALID);
        }

}
