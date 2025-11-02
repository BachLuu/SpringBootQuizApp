package com.example.SpringBootWeb.services.interfaces;

import com.example.SpringBootWeb.entities.models.RefreshToken;

public interface IRefreshTokenService {

    RefreshToken saveRefreshToken(String username, RefreshToken refreshToken);

}
