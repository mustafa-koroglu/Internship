package com.example.backend.request; // Request paketi

public class RegisterRequest { // Kayıt isteği DTO sınıfı
    private String username; // Kullanıcı adı

    private String password; // Kullanıcı şifresi

    private String role; // Kullanıcı rolü

    public String getUsername() { return username; } // Kullanıcı adı getter
    public void setUsername(String username) { this.username = username; } // Kullanıcı adı setter
    public String getPassword() { return password; } // Şifre getter
    public void setPassword(String password) { this.password = password; } // Şifre setter
    public String getRole() { return role; } // Rol getter
    public void setRole(String role) { this.role = role; } // Rol setter
}