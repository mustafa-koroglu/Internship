// Bu dosya, kullanıcı giriş yanıtı için kullanılan DTO'yu tanımlar.
package com.example.backend.response;

/**
 * Kullanıcı giriş yanıtı için kullanılan veri transfer nesnesi (DTO).
 * JWT token, kullanıcı adı ve rolü içerir.
 */
public class LoginResponse {
    /** Kullanıcıya verilen JWT token */
    private String token;
    /** Kullanıcı adı */
    private String username;
    /** Kullanıcı rolü (ör: ADMIN, USER) */
    private String role;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
} 