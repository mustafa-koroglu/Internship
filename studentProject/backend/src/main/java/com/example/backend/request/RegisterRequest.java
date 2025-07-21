package com.example.backend.request;

/**
 * Kullanıcı kaydı için kullanılan veri transfer nesnesi (DTO).
 * Admin tarafından yeni kullanıcı eklerken kullanılır.
 */
public class RegisterRequest {
    /** Kullanıcı adı */
    private String username;
    /** Kullanıcı şifresi */
    private String password;
    /** Kullanıcı rolü (ADMIN veya USER) */
    private String role;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}