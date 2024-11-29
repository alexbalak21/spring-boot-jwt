
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

    // Injecting secret keys and expiration times from application properties
    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.refresh-secret-key}")
    private String refreshSecretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    @Value("${security.jwt.refresh-expiration-time}")
    private long refreshExpiration;

    // Constructor to initialize UserService
    public JwtService(UserService userService) {
        this.userService = userService;
    }

    // Extracts the username from the JWT token
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject, secretKey);
    }

    // Extracts a specific claim from the JWT token
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, String key) {
        final Claims claims = extractAllClaims(token, key);
        return claimsResolver.apply(claims);
    }

    // Generates a JWT token for the given user details
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    // Generates a JWT token with additional claims
    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return buildToken(extraClaims, userDetails, jwtExpiration, secretKey);
    }

    // Returns the expiration time for the JWT token
    public long getExpirationTime() {
        return jwtExpiration;
    }

    // Validates the JWT token against the user details
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()));
    }

    // Returns the expiration time for the refresh token
    public long getRefreshExpirationTime() {
        return refreshExpiration;
    }

    // Generates a refresh token for the authenticated user
    public String generateRefreshToken(User authenticatedUser) {
        return buildRefreshToken(userService.generateUuid(authenticatedUser.getEmail()));
    }

    // Builds a JWT token with the specified claims, user details, expiration time, and key
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

    // Builds a refresh token with the specified UUID
    public String buildRefreshToken(UUID uuid) {
        return Jwts
                .builder()
                .claim("uuid", uuid.toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(getSignInKey(refreshSecretKey), SignatureAlgorithm.HS256)
                .compact();
    }

    // Checks if the JWT token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token, secretKey).before(new Date());
    }

    // Extracts the expiration date from the JWT token
    private Date extractExpiration(String token, String key) {
        return extractClaim(token, Claims::getExpiration, key);
    }

    // Extracts all claims from the JWT token
    private Claims extractAllClaims(String token, String key) {
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Decodes the secret key and returns it as a Key object
    private Key getSignInKey(String key) {
        byte[] keyBytes = Decoders.BASE64.decode(key);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}