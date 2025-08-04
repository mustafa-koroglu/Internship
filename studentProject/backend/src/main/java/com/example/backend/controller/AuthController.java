package com.example.backend.controller;

import com.example.backend.dataAccess.AppUserRepository;
import com.example.backend.entities.AppUser;
import com.example.backend.request.LoginRequest;
import com.example.backend.request.RegisterRequest;
import com.example.backend.response.LoginResponse;
import com.example.backend.utility.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AppUserRepository appUserRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Şifre yanlış");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRole(user.getRole());
        response.setUsername(user.getUsername());
        return response;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest registerRequest, @RequestHeader("Authorization") String authHeader) {
        String token = authHeader != null && authHeader.startsWith("Bearer ") ? authHeader.substring(7) : null;
        if (token == null) throw new RuntimeException("Yetkisiz erişim");
        String role = jwtUtil.extractRole(token);
        if (!"ADMIN".equals(role)) throw new RuntimeException("Sadece admin kullanıcı ekleyebilir");
        if (appUserRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Kullanıcı adı zaten mevcut");
        }
        AppUser user = new AppUser();
        user.setUsername(registerRequest.getUsername());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setRole(registerRequest.getRole());
        appUserRepository.save(user);
        return "Kullanıcı başarıyla kaydedildi";
    }
}