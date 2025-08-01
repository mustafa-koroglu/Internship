package com.example.backend.request; // Request paketi

public class LoginRequest { // Giriş isteği DTO sınıfı
    private String username; // Kullanıcı adı

    private String password; // Kullanıcı şifresi

    public String getUsername() { return username; } // Kullanıcı adı getter
    public void setUsername(String username) { this.username = username; } // Kullanıcı adı setter
    public String getPassword() { return password; } // Şifre getter
    public void setPassword(String password) { this.password = password; } // Şifre setter
} 