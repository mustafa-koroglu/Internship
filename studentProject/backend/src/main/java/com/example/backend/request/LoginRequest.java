// Bu dosya, kullanıcı giriş isteği için kullanılan DTO'yu tanımlar.
package com.example.backend.request;

/**
 * Kullanıcı giriş isteği için kullanılan veri transfer nesnesi (DTO).
 * Kullanıcı adı ve şifre ile giriş yapılır.
 */
public class LoginRequest {
    /** Kullanıcı adı */
    private String username;
    /** Kullanıcı şifresi */
    private String password;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
} 