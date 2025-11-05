package com.example.SpringBootWeb.services.impl;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.SpringBootWeb.dtos.auths.LoginRequestDto;
import com.example.SpringBootWeb.dtos.auths.LoginResponseDto;
import com.example.SpringBootWeb.dtos.auths.RegisterRequestDto;
import com.example.SpringBootWeb.dtos.users.UserResponseDto;
import com.example.SpringBootWeb.entities.jwt.JwtProperties;
import com.example.SpringBootWeb.entities.models.RefreshToken;
import com.example.SpringBootWeb.entities.models.User;
import com.example.SpringBootWeb.repositories.RefreshTokenRepository;
import com.example.SpringBootWeb.repositories.UserRepository;
import com.example.SpringBootWeb.services.interfaces.IAuthService;
import com.example.SpringBootWeb.services.interfaces.IRefreshTokenService;
import com.example.SpringBootWeb.services.jwt.JwtTokenUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService implements IAuthService {

        private final UserRepository userRepository;
        private final CustomUserDetailsService customUserDetailsService;
        private final JwtTokenUtil jwtTokenUtil;
        private final AuthenticationManager authenticationManager;
        private final PasswordEncoder passwordEncoder;
        private final IRefreshTokenService refreshTokenService;
        private final RefreshTokenRepository refreshTokenRepository;
        private final JwtProperties jwtProperties;

        @Override
        public LoginResponseDto login(LoginRequestDto request, HttpServletResponse response) {
                authenticationManager.authenticate(
                                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

                final UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
                final String accessToken = jwtTokenUtil.generateToken(userDetails);
                final RefreshToken refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
                refreshTokenService.saveRefreshToken(userDetails.getUsername(), refreshToken);

                setTokenToHttpCookiesHeader(accessToken, refreshToken, response, jwtProperties.expiration(),
                                jwtProperties.refreshExpiration());
                return LoginResponseDto.builder()
                                .accessToken(accessToken)
                                .refreshToken(refreshToken)
                                .build();
        }

        @Override
        public void register(RegisterRequestDto request) {
                User user = new User();
                user.setEmail(request.getEmail());
                user.setPassword(passwordEncoder.encode(request.getPassword()));
                userRepository.save(user);
        }

        @Override
        public UserResponseDto getCurrentUser(String accessToken) {
                String email = jwtTokenUtil.extractUserSubject(accessToken);
                // Tìm user trong database
                User user = userRepository.findByEmail(email)
                                .orElseThrow(() -> new RuntimeException("User not found"));

                // Map sang UserDto
                Set<UserResponseDto.RoleDto> roleDtos = user.getRoles().stream()
                                .map(role -> UserResponseDto.RoleDto.builder()
                                                .name(role.getName())
                                                .isActive(role.getIsActive())
                                                .build())
                                .collect(Collectors.toSet());

                return UserResponseDto.builder()
                                .id(user.getId())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .avatar(user.getAvatar())
                                .dateOfBirth(user.getDateOfBirth())
                                .isActive(user.getIsActive())
                                .createdAt(user.getCreatedAt())
                                .roles(roleDtos)
                                .displayName(user.getFirstName() + " " + user.getLastName())
                                .build();
        }

        @Override
        public void logout(HttpServletRequest request, HttpServletResponse response) {
                // Extract refresh token from cookies
                String refreshToken = null;
                if (request.getCookies() != null) {
                        for (Cookie cookie : request.getCookies()) {
                                if ("refresh_token".equals(cookie.getName())) {
                                        refreshToken = cookie.getValue();
                                        break;
                                }
                        }
                }

                // Delete refresh token from database
                if (refreshToken != null) {
                        refreshTokenRepository.findByToken(refreshToken)
                                        .ifPresent(refreshTokenRepository::delete);
                }
                setTokenToHttpCookiesHeader("", null, response, Long.parseLong("0"), Long.parseLong("0"));
        }

        // ------------------------ Private methods -------------------------//

        private void setTokenToHttpCookiesHeader(final String accessToken, final RefreshToken refreshToken,
                        HttpServletResponse response, final long accessTokenExpiry, final long refreshTokenExpiry) {
                ResponseCookie accessCookie = ResponseCookie.from("access_token", accessToken)
                                .httpOnly(true)
                                .secure(true)
                                .path("/")
                                .maxAge(accessTokenExpiry)
                                .sameSite("None")
                                .build();

                // Handle null RefreshToken
                String refreshTokenValue = (refreshToken != null) ? refreshToken.getToken() : "";
                ResponseCookie refreshCookie = ResponseCookie.from("refresh_token", refreshTokenValue)
                                .httpOnly(true)
                                .secure(true)
                                .path("/api/auth/refresh")
                                .maxAge(refreshTokenExpiry)
                                .sameSite("None")
                                .build();
                response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
                response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());
        }
}
