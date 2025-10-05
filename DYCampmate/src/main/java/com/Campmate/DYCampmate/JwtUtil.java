package com.Campmate.DYCampmate;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;

@Component
@Service
public class JwtUtil {
//    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long expiration = 1000 * 60 * 60 * 3; // 3시간
    private final String secret = "My-longerLonger_String-s=c-r=e-t";
    private final Key key = Keys.hmacShaKeyFor(secret.getBytes());

    //normalLogin
    public String generateToken(String customerId) {
        return Jwts.builder()
                .setSubject(customerId)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    //socialLogin
    //AdminLogin
    public String createToken(String Id, String email) {
        return Jwts.builder()
                .setSubject(Id)
                .claim("email", email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // 3시간
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String getCustomerIdFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
