// Bu dosya, kullanıcı giriş yanıtı için kullanılan DTO'yu tanımlar.
package com.example.backend.response;

// Kullanıcı giriş yanıtı için kullanılan veri transfer nesnesi
public class LoginResponse {
    // Kullanıcıya verilen JWT token
    private String token;
    // Kullanıcı adı
    private String username;
    // Kullanıcı rolü (ör: ADMIN, USER)
    private String role;
    // Token bilgisini döndüren getter metodu
    public String getToken() { return token; }
    // Token bilgisini ayarlayan setter metodu
    public void setToken(String token) { this.token = token; }
    // Kullanıcı adını döndüren getter metodu
    public String getUsername() { return username; }
    // Kullanıcı adını ayarlayan setter metodu
    public void setUsername(String username) { this.username = username; }
    // Kullanıcı rolünü döndüren getter metodu
    public String getRole() { return role; }
    // Kullanıcı rolünü ayarlayan setter metodu
    public void setRole(String role) { this.role = role; }
} 