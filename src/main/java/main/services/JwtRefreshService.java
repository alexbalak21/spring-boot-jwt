package main.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import main.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtRefreshService {
    private final ConnectionService connectionService;

    @Value("${security.jwt.refresh-secret-key}")
    private String refreshSecretKey;

    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpiration;

    public JwtRefreshService(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    private Key getRefreshSignInKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateRefreshToken(User authenticatedUser) {
        UUID uuid = connectionService.generateConnection(authenticatedUser.getId());
        return buildRefreshToken(authenticatedUser.getId(), uuid);
    }


    public String buildRefreshToken(Integer userId, UUID uuid) {
        return Jwts
                .builder()
                .setSubject(userId.toString())
                .claim("uuid", uuid.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getRefreshSignInKey(refreshSecretKey), SignatureAlgorithm.HS256)
                .compact();
    }
}
