package com.example.backend.controller; // Controller paketi

import com.example.backend.dataAccess.AppUserRepository; // Kullanıcı repository'si
import com.example.backend.entities.AppUser; // Kullanıcı entity'si
import com.example.backend.request.LoginRequest; // Giriş isteği
import com.example.backend.request.RegisterRequest; // Kayıt isteği
import com.example.backend.response.LoginResponse; // Giriş yanıtı
import com.example.backend.utility.JwtUtil; // JWT utility

import lombok.RequiredArgsConstructor; // Constructor injection
import org.springframework.security.crypto.password.PasswordEncoder; // Şifre encoder'ı
import org.springframework.web.bind.annotation.*; // Web controller anotasyonları

@RestController // REST controller anotasyonu
@RequestMapping("/auth") // Auth endpoint prefix'i
@RequiredArgsConstructor // Constructor injection
public class AuthController { // Kimlik doğrulama controller sınıfı

    private final AppUserRepository appUserRepository; // Kullanıcı repository'si
    private final JwtUtil jwtUtil; // JWT utility
    private final PasswordEncoder passwordEncoder; // Şifre encoder'ı

    @PostMapping("/login") // Giriş endpoint'i
    public LoginResponse login(@RequestBody LoginRequest loginRequest) { // Giriş metodu
        String username = loginRequest.getUsername(); // Kullanıcı adını al
        String password = loginRequest.getPassword(); // Şifreyi al

        AppUser user = appUserRepository.findByUsername(username) // Kullanıcıyı bul
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı")); // Kullanıcı yoksa hata fırlat

        if (!passwordEncoder.matches(password, user.getPassword())) { // Şifreyi kontrol et
            throw new RuntimeException("Şifre yanlış"); // Şifre yanlışsa hata fırlat
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole()); // JWT token üret

        LoginResponse response = new LoginResponse(); // Yanıt nesnesi oluştur
        response.setToken(token); // Token'ı ayarla
        response.setRole(user.getRole()); // Rolü ayarla
        response.setUsername(user.getUsername()); // Kullanıcı adını ayarla
        return response; // Yanıtı döndür
    }

    @PostMapping("/register") // Kayıt endpoint'i
    public String register(@RequestBody RegisterRequest registerRequest, @RequestHeader("Authorization") String authHeader) { // Kayıt metodu
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null; // Token'ı çıkar
        if (token == null) throw new RuntimeException("Yetkisiz erişim"); // Token yoksa hata fırlat
        String role = jwtUtil.extractRole(token); // Token'dan rolü çıkar
        if (!"ADMIN".equals(role)) throw new RuntimeException("Sadece admin kullanıcı ekleyebilir"); // Admin değilse hata fırlat
        if (appUserRepository.findByUsername(registerRequest.getUsername()).isPresent()) { // Kullanıcı adı kontrolü
            throw new RuntimeException("Kullanıcı adı zaten mevcut"); // Kullanıcı adı varsa hata fırlat
        }
        AppUser user = new AppUser(); // Yeni kullanıcı oluştur
        user.setUsername(registerRequest.getUsername()); // Kullanıcı adını ayarla
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Şifreyi encode et ve ayarla
        user.setRole(registerRequest.getRole()); // Rolü ayarla
        appUserRepository.save(user); // Kullanıcıyı kaydet
        return "Kullanıcı başarıyla kaydedildi"; // Başarı mesajı döndür
    }
}