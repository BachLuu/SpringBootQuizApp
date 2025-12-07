package com.example.SpringBootWeb.services.jwt;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import com.example.SpringBootWeb.entities.constants.ErrorMessage;
import com.example.SpringBootWeb.entities.models.User;
import com.example.SpringBootWeb.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.SpringBootWeb.entities.jwt.JwtProperties;
import com.example.SpringBootWeb.entities.models.RefreshToken;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtTokenUtil {
    private final JwtProperties props;
    private final UserRepository userRepository;

    private JWSSigner signer;
    private JWSVerifier verifier;

    @PostConstruct
    public void init() {
        try {
            this.signer = new MACSigner(props.secret());
            this.verifier = new MACVerifier(props.secret());
        } catch (JOSEException e) {
            throw new RuntimeException("Could not initialize JWT signer/verifier", e);
        }
    }

    public String extractUserSubject(String token) {
        return extractClaim(token, JWTClaimsSet::getSubject);
    }

    public <T> T extractClaim(String token, Function<JWTClaimsSet, T> claimsResolver) {
        final JWTClaimsSet claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }

    public Boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUserSubject(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    private String createToken(Map<String, Object> claims, String subject) {
        try {
            Date now = new Date();
            Date expiration = new Date(now.getTime() + props.expiration());

            JWTClaimsSet.Builder claimsBuilder = new JWTClaimsSet.Builder().subject(subject)
                    .jwtID(UUID.randomUUID().toString()).issueTime(now).expirationTime(expiration);

            for (Map.Entry<String, Object> entry : claims.entrySet()) {
                claimsBuilder.claim(entry.getKey(), entry.getValue());
            }

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsBuilder.build());

            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error creating JWT token", e);
        }
    }

    private boolean isTokenExpired(String token) {
        Date expiration = extractExpiration(token);
        return expiration != null && expiration.before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, JWTClaimsSet::getExpirationTime);
    }

    private JWTClaimsSet extractAllClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);

            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("Invalid JWT signature");
            }

            return signedJWT.getJWTClaimsSet();
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Invalid JWT token", e);
        }
    }

    public RefreshToken generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        String token = createToken(claims, userDetails.getUsername());
        User user = userRepository.findByEmail(userDetails.getUsername()).orElseThrow(
                () -> new UsernameNotFoundException(ErrorMessage.USER_NOT_FOUND + userDetails.getUsername()));

        return RefreshToken.builder().token(token).user(user)
                .expiryDate(Instant.now().plusMillis(props.refreshExpiration())).build();
    }
}