package com.example.springbootweb.services.interfaces;

import com.example.springbootweb.entities.models.RefreshToken;
import org.springframework.data.util.Pair;

public interface IRefreshTokenService {

    RefreshToken saveRefreshToken(String username, RefreshToken refreshToken);

    Pair<Boolean, String> validateRefreshToken(String token);
}
