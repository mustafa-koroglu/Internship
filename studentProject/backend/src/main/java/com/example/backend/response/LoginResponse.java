package com.example.backend.response; // Response paketi

public class LoginResponse { // Giriş yanıtı DTO sınıfı
    private String token; // JWT token

    private String username; // Kullanıcı adı

    private String role; // Kullanıcı rolü

    public String getToken() { return token; } // Token getter
    public void setToken(String token) { this.token = token; } // Token setter
    public String getUsername() { return username; } // Kullanıcı adı getter
    public void setUsername(String username) { this.username = username; } // Kullanıcı adı setter
    public String getRole() { return role; } // Rol getter
    public void setRole(String role) { this.role = role; } // Rol setter
} 