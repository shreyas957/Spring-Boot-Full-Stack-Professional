package com.shreyas.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class JWTUtil {      // This Class is used to handel various aspects of JWT

    //    private static final SecretKey secretKey = Jwts.SIG.HS256.key().build();       // Gives SecretKey
//    private static final String SECRET_KEY2 = Encoders.BASE64.encode(secretKey.getEncoded());   // Store the key using BASE64 Encode
    private static final String SECRET_KEY = "E88Wuor/eGkI+Q1HxIQkS1EmIybZJiH5DPWPN0s51H4=";

    // Generate JWT Token
    public String issueToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer("Shreyas Shevale")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(15, ChronoUnit.DAYS)))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, List<String> scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String getSubject(String token) {
        return getClaims(token).getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
//        return Keys.hmacShaKeyFor(secretKey.getEncoded());
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    public boolean isTokenValid(String jwt, String username) {
        String subject = getSubject(jwt);
        return subject.equals(username) && !isTokenExpired(jwt);
    }

    private boolean isTokenExpired(String jwt) {
        Date today = Date.from(Instant.now());
        return getClaims(jwt)
                .getExpiration()
                .before(
                        today
                );
    }
}
