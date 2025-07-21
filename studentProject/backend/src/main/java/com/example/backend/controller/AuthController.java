
package com.example.backend.controller;


import com.example.backend.dataAccess.AppUserRepository;
import com.example.backend.entities.AppUser;
import com.example.backend.request.LoginRequest;
import com.example.backend.request.RegisterRequest;
import com.example.backend.response.LoginResponse;
import com.example.backend.utility.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Kullanıcı kimlik doğrulama ve kullanıcı ekleme işlemlerini yöneten controller.
 * /auth/login: Giriş işlemi
 * /auth/register: Sadece adminlerin yeni kullanıcı eklemesi
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    // Kullanıcı veritabanı işlemleri için repository
    @Autowired
    private AppUserRepository appUserRepository;

    // JWT işlemleri için yardımcı sınıf
    @Autowired
    private JwtUtil jwtUtil;

    // Şifreleri hashlemek için encoder
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Kullanıcı giriş işlemi (POST /auth/login)
     * @param loginRequest Kullanıcı adı ve şifre
     * @return JWT token, rol ve kullanıcı adı
     */
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // Kullanıcıyı veritabanında bul
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Şifreyi kontrol et
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Şifre yanlış");
        }

        // JWT token üret
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // Yanıtı hazırla
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRole(user.getRole());
        response.setUsername(user.getUsername());
        return response;
    }

    /**
     * Sadece ADMIN'lerin kullanıcı ekleyebileceği endpoint (POST /auth/register)
     * @param registerRequest Yeni kullanıcı bilgileri
     * @param authHeader Admin'in JWT token'ı
     * @return Başarı mesajı
     */
    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest, @RequestHeader("Authorization") String authHeader) {
        // Token'dan rolü çıkar
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        if (token == null) throw new RuntimeException("Yetkisiz erişim");
        String role = jwtUtil.extractRole(token);
        if (!"ADMIN".equals(role)) throw new RuntimeException("Sadece admin kullanıcı ekleyebilir");
        // Kullanıcı adı benzersiz olmalı
        if (appUserRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Kullanıcı adı zaten mevcut");
        }
        // Yeni kullanıcıyı oluştur ve kaydet
        AppUser user = new AppUser();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        appUserRepository.save(user);
        return "Kullanıcı başarıyla kaydedildi";
    }
}