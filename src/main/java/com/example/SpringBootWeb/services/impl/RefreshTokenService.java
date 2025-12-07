package com.example.SpringBootWeb.services.impl;

import java.time.Instant;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
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
import com.example.SpringBootWeb.services.jwt.JwtTokenUtil;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RefreshTokenService implements IRefreshTokenService {
        private final RefreshTokenRepository refreshTokenRepository;
        private final UserRepository userRepository;
        private final JwtTokenUtil jwtTokenUtil;
        private final CustomUserDetailsService customUserDetailsService;
        private static final Logger logger = LoggerFactory.getLogger(RefreshTokenService.class);

        @Override
        @Transactional
        public RefreshToken saveRefreshToken(String email, RefreshToken refreshToken) {

                final User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND + email));
                UserDetails userDetails = new org.springframework.security.core.userdetails.User(user.getEmail(),
                                user.getPassword(),
                                user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName()))
                                                .collect(Collectors.toList())); // Một user chỉ giữ 1 refresh token: xóa
                                                                                // cái cũ nếu có
                refreshTokenRepository.deleteByUser(user);
                refreshTokenRepository.flush();

                RefreshToken newRefreshToken = jwtTokenUtil.generateRefreshToken(userDetails);

                return refreshTokenRepository.save(newRefreshToken);
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
