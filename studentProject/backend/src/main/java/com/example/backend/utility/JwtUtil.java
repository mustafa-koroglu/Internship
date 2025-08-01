package com.example.backend.utility; // Utility paketi

import io.jsonwebtoken.*; // JWT anotasyonları
import io.jsonwebtoken.security.Keys; // JWT anahtar sınıfı
import org.springframework.stereotype.Component; // Component anotasyonu
import java.security.Key; // Güvenlik anahtarı
import java.util.Date; // Tarih sınıfı
import java.util.HashMap; // Hash map
import java.util.Map; // Map

@Component // Spring component anotasyonu
public class JwtUtil { // JWT utility sınıfı
    private final String SECRET_KEY = "mysecretkeymysecretkeymysecretkeymysecretkey"; // JWT imzalama anahtarı
    private final long EXPIRATION_TIME = 1000 * 60 * 60 * 10; // Token geçerlilik süresi (10 saat)

    private Key getSigningKey() { // İmzalama anahtarını döndürme metodu
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes()); // HMAC SHA anahtarı oluştur
    }

    public String generateToken(String username, String role) { // JWT token üretme metodu
        Map<String, Object> claims = new HashMap<>(); // Claim'ler map'i oluştur
        claims.put("role", role); // Rolü claim'e ekle
        return Jwts.builder() // JWT builder başlat
                .setClaims(claims) // Claim'leri ayarla
                .setSubject(username) // Subject'i ayarla
                .setIssuedAt(new Date(System.currentTimeMillis())) // Oluşturma tarihini ayarla
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Bitiş tarihini ayarla
                .signWith(getSigningKey(), SignatureAlgorithm.HS256) // İmzala
                .compact(); // Token'ı oluştur
    }

    public String extractUsername(String token) { // Token'dan kullanıcı adını çıkarma metodu
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build() // JWT parser oluştur
                .parseClaimsJws(token).getBody().getSubject(); // Subject'i al
    }

    public String extractRole(String token) { // Token'dan rol bilgisini çıkarma metodu
        return (String) Jwts.parserBuilder().setSigningKey(getSigningKey()).build() // JWT parser oluştur
                .parseClaimsJws(token).getBody().get("role"); // Rolü al
    }

    public boolean validateToken(String token, String username) { // Token doğrulama metodu
        final String extractedUsername = extractUsername(token); // Token'dan kullanıcı adını çıkar
        return (extractedUsername.equals(username) && !isTokenExpired(token)); // Kullanıcı adı eşleşiyor mu ve süresi dolmamış mı kontrol et
    }

    private boolean isTokenExpired(String token) { // Token süresi kontrol metodu
        Date expiration = Jwts.parserBuilder().setSigningKey(getSigningKey()).build() // JWT parser oluştur
                .parseClaimsJws(token).getBody().getExpiration(); // Bitiş tarihini al
        return expiration.before(new Date()); // Süresi dolmuş mu kontrol et
    }
}