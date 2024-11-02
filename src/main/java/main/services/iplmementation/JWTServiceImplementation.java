package main.services.iplmementation;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Service
public class JWTServiceImplementation {

    private String generateToken(UserDetails userDetails){
        return Jwts.builder().subject(userDetails.getUsername())
                 .issuedAt(new Date(System.currentTimeMillis()))
                 .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                 .signWith(getSignKey())
                 .compact();
    }
    private <T> T extractClaim (String token, Function<Claims, T> claimsResolver){
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token){
        return Jwts.parser().setSigningKey(getSignKey()).build().parseSignedClaims(token).getPayload();
    }



    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode("x4rs6LlwA6s+Gy0BfShbKZfj+5Jh+PoTKhS7v8GzEqo=");
        return Keys.hmacShaKeyFor(key);
    }
}
