package main.services;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import main.entities.User;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class JwtService {
    private final UserService userService;
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.refresh-secret-key}")
    private String refreshSecretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpiration;

    public JwtService(UserService userService) {
        this.userService = userService;
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    public String extractRefreshUsername(String token) {
        return extractClaim(token, Claims::getSubject, refreshSecretKey);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration, secretKey);
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, secretKey);
    }

    public boolean isRefreshTokenValid(String token, UserDetails userDetails) {
        final String username = extractRefreshUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token, refreshSecretKey);
    }

    public long getRefreshExpirationTime() {
        return refreshExpiration;
    }

    public String generateRefreshToken(User authenticatedUser) {
        return buildRefreshToken(userService.generateUuid(authenticatedUser.getEmail()));
    }

    private String buildToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails,
            long expiration,
            String key
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(key), SignatureAlgorithm.HS256)
                .compact();
    }


    public String buildRefreshToken(UUID uuid) {
        return Jwts
                .builder()
                .claim("uuid", uuid.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSignInKey(refreshSecretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    private boolean isTokenExpired(String token, String key) {
        return extractExpiration(token, key).before(new Date());
    }

    private Date extractExpiration(String token, String key) {
        return extractClaim(token, Claims::getExpiration, key);
    }

    private Claims extractAllClaims(String token, String key) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
