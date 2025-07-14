// Bu dosya, kullanıcı giriş isteği için kullanılan DTO'yu tanımlar.
package com.example.backend.request;

// Kullanıcı giriş isteği için kullanılan veri transfer nesnesi
public class LoginRequest {
    // Kullanıcı adı
    private String username;
    // Kullanıcı şifresi
    private String password;
    // Kullanıcı adını döndüren getter metodu
    public String getUsername() { return username; }
    // Kullanıcı adını ayarlayan setter metodu
    public void setUsername(String username) { this.username = username; }
    // Kullanıcı şifresini döndüren getter metodu
    public String getPassword() { return password; }
    // Kullanıcı şifresini ayarlayan setter metodu
    public void setPassword(String password) { this.password = password; }
} 