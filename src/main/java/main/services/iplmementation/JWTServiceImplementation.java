package main.services.iplmementation;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Service
public class JWTServiceImplementation {

    private String generateToken(UserDetails userDetails){
        return Jwts.builder().subject(userDetails.getUsername())
                 .issuedAt(new Date(System.currentTimeMillis()))
                 .expiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24))
                 .signWith(getSignKey())
                 .compact();
    }

    private Key getSignKey() {
        byte[] key = Decoders.BASE64.decode("x4rs6LlwA6s+Gy0BfShbKZfj+5Jh+PoTKhS7v8GzEqo=");
        return Keys.hmacShaKeyFor(key);
    }
}
