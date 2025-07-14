// Bu dosya, JWT token üretimi ve doğrulaması için yardımcı metotlar içerir.
package com.example.backend.utility;

// JWT işlemleri için gerekli kütüphaneleri import eder
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

// Anahtar ve tarih işlemleri için gerekli kütüphaneleri import eder
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

// Bu sınıf bir Spring bileşenidir (bean olarak yönetilir)
@Component
public class JwtUtil {
    // JWT imzalama anahtarı (en az 32 karakter olmalı)
    private final String SECRET_KEY = "mysecretkeymysecretkeymysecretkeymysecretkey";
    // Token geçerlilik süresi (10 saat)
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10;

    // İmzalama anahtarını döndüren yardımcı metot
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

    // Kullanıcı adı ve rol ile JWT token üretir
    public String generateToken(String username, String role) {
        // Token içine eklenecek ek bilgiler (claims)
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        // Token'ı oluşturur ve imzalar
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // Token'dan kullanıcı adını (subject) çıkarır
    public String extractUsername(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getSubject();
    }

    // Token'dan rol bilgisini çıkarır
    public String extractRole(String token) {
        return (String) Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().get("role");
    }

    // Token'ın geçerli olup olmadığını ve kullanıcı adıyla eşleşip eşleşmediğini kontrol eder
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
    }

    // Token'ın süresinin dolup dolmadığını kontrol eder
    private boolean isTokenExpired(String token) {
        Date expiration = Jwts.parserBuilder().setSigningKey(getSigningKey()).build()
                .parseClaimsJws(token).getBody().getExpiration();
        return expiration.before(new Date());
    }
}