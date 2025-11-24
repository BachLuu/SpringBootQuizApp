package com.example.SpringBootWeb.services.interfaces;

import com.example.SpringBootWeb.entities.models.RefreshToken;
import org.springframework.data.util.Pair;

public interface IRefreshTokenService {

    RefreshToken saveRefreshToken(String username, RefreshToken refreshToken);

    Pair<Boolean, String> validateRefreshToken(String token);
}
