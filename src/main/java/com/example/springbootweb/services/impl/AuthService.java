package com.example.springbootweb.services.impl;

import org.springframework.data.util.Pair;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.springbootweb.entities.constants.ErrorMessage;
import com.example.springbootweb.entities.dtos.auths.LoginRequestDto;
import com.example.springbootweb.entities.dtos.auths.LoginResponseDto;
import com.example.springbootweb.entities.dtos.auths.RegisterRequestDto;
import com.example.springbootweb.entities.dtos.users.UserDetailResponse;
import com.example.springbootweb.entities.jwt.JwtProperties;
import com.example.springbootweb.entities.models.RefreshToken;
import com.example.springbootweb.entities.models.User;
import com.example.springbootweb.exceptions.ResourceNotFoundException;
import com.example.springbootweb.mappers.UserMapper;
import com.example.springbootweb.repositories.RefreshTokenRepository;
import com.example.springbootweb.repositories.UserRepository;
import com.example.springbootweb.services.interfaces.IAuthService;
import com.example.springbootweb.services.interfaces.IRefreshTokenService;
import com.example.springbootweb.services.jwt.JwtTokenUtil;

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
  private final UserMapper userMapper;

  @Override
  public LoginResponseDto login(LoginRequestDto request, HttpServletResponse response) {
    authenticationManager
        .authenticate(new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

    final UserDetails userDetails = customUserDetailsService.loadUserByUsername(request.getEmail());
    final String accessToken = jwtTokenUtil.generateToken(userDetails);
    final RefreshToken refreshToken = jwtTokenUtil.generateRefreshToken(userDetails);
    refreshTokenService.saveRefreshToken(userDetails.getUsername(), refreshToken);

    jwtTokenUtil.setTokenToHttpCookiesHeader(accessToken, refreshToken, response, jwtProperties.expiration(),
        jwtProperties.refreshExpiration());
    return LoginResponseDto.builder().accessToken(accessToken).refreshToken(refreshToken).build();
  }

  @Override
  public void register(RegisterRequestDto request) {
    User user = User.builder()
        .email(request.getEmail())
        .dateOfBirth(request.getDateOfBirth())
        .firstName(request.getFirstName())
        .lastName(request.getLastName())
        .password(passwordEncoder.encode(request.getPassword()))
        .build();
    userRepository.save(user);
  }

  @Override
  public UserDetailResponse getCurrentUser(String accessToken) {
    String email = jwtTokenUtil.extractUserSubject(accessToken);
    // Tìm user trong database
    User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException(ErrorMessage.USER_NOT_FOUND));

    return userMapper.toResponse(user);
  }

  @Override
  public void logout(HttpServletRequest request, HttpServletResponse response) {
    // Extract refresh token from cookies
    String refreshToken = null;
    if (request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        if (JwtTokenUtil.REFRESH_TOKEN.equals(cookie.getName())) {
          refreshToken = cookie.getValue();
          break;
        }
      }
    }

    // Delete refresh token from database
    if (refreshToken != null) {
      refreshTokenRepository.findByToken(refreshToken).ifPresent(refreshTokenRepository::delete);
    }
    jwtTokenUtil.setTokenToHttpCookiesHeader("", null, response, Long.parseLong("0"), Long.parseLong("0"));
  }

  @Override
  public Pair<Boolean, String> refreshToken(HttpServletRequest request, HttpServletResponse response) {
    final String refreshToken = jwtTokenUtil.getRefreshTokenFromCookie(request);
    final Pair<Boolean, String> validationResult = refreshTokenService.validateRefreshToken(refreshToken);
    if (validationResult.getFirst()) {
      RefreshToken existingRefreshToken = refreshTokenRepository.findByToken(refreshToken)
          .orElseThrow(() -> new ResourceNotFoundException(ErrorMessage.REFRESH_TOKEN_NOT_FOUND));
      String email = existingRefreshToken.getUser().getEmail();
      final UserDetails userDetails = customUserDetailsService.loadUserByUsername(email);
      final String newAccessToken = jwtTokenUtil.generateToken(userDetails);

      // Set new tokens in cookies
      jwtTokenUtil.setTokenToHttpCookiesHeader(newAccessToken, existingRefreshToken, response,
          jwtProperties.expiration(),
          jwtProperties.refreshExpiration());

      return Pair.of(true, newAccessToken);
    }
    return Pair.of(true, validationResult.getSecond());
  }
}
