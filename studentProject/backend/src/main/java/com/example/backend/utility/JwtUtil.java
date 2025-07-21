// Bu dosya, JWT token üretimi ve doğrulaması için yardımcı metotlar içerir.
package com.example.backend.utility;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT token üretimi ve doğrulaması için yardımcı metotlar içerir.
 * Kullanıcı adı ve rol ile token üretir, token'dan bilgi çıkarır ve doğrulama yapar.
 */
@Component
public class JwtUtil {
    /** JWT imzalama anahtarı (en az 32 karakter olmalı) */
    private final String SECRET_KEY = "mysecretkeymysecretkeymysecretkeymysecretkey";
    /** Token geçerlilik süresi (10 saat) */
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    /**
     * İmzalama anahtarını döndüren yardımcı metot
     * @return Key nesnesi
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    /**
     * Kullanıcı adı ve rol ile JWT token üretir
     * @param username Kullanıcı adı
     * @param role Kullanıcı rolü
     * @return JWT token
     */
    public String generateToken(String username, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Token'dan kullanıcı adını (subject) çıkarır
     * @param token JWT token
     * @return Kullanıcı adı
     */
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Token'dan rol bilgisini çıkarır
     * @param token JWT token
     * @return Kullanıcı rolü
     */
    public String extractRole(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().get("role");
    }

    /**
     * Token'ın geçerli olup olmadığını ve kullanıcı adıyla eşleşip eşleşmediğini kontrol eder
     * @param token JWT token
     * @param username Beklenen kullanıcı adı
     * @return true: geçerli, false: geçersiz
     */
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    /**
     * Token'ın süresinin dolup dolmadığını kontrol eder
     * @param token JWT token
     * @return true: süresi dolmuş, false: geçerli
     */
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }
}