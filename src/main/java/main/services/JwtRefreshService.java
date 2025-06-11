package main.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import main.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

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

    private Claims extractAllClaims(String token) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getRefreshSignInKey(refreshSecretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String key) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }



    public String generateRefreshToken(User authenticatedUser) {
        UUID uuid = connectionService.generateConnection(authenticatedUser.getId());
        return buildRefreshToken(uuid);
    }

    public String buildRefreshToken(UUID uuid) {
        if (uuid == null) return null;
        return Jwts
                .builder()
                .setSubject(uuid.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getRefreshSignInKey(refreshSecretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    // Extracts the UUID from Refresh Token
    private UUID extractUuid(String token) {
        String uuid = extractClaim(token, Claims::getSubject, refreshSecretKey);
        return UUID.fromString(uuid);
    }


    public boolean validateRefreshToken(String refreshToken, String userEmail) {
        return connectionService.validateConnection(extractUuid(refreshToken), userEmail);
    }

    public String renewRefreshToken(String refreshToken, String userEmail) {
        UUID uniqueConnectionId = extractUuid(refreshToken);
        if (connectionService.validateConnection(uniqueConnectionId, userEmail)) {
            Optional<UUID> optionalUuid = connectionService.renewConnection(uniqueConnectionId, userEmail);
            UUID newUuid = optionalUuid.orElse(null);
            return buildRefreshToken(newUuid);
        }

        return null;

    }



    private Key getRefreshSignInKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
