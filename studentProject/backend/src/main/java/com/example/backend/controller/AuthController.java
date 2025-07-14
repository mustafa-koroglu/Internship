// Bu dosya, kullanıcı kimlik doğrulama işlemlerini yöneten controller sınıfını tanımlar.
package com.example.backend.controller;

// Kullanıcı repository'si, entity'si ve JWT yardımcı sınıfını import eder
import com.example.backend.dataAccess.AppUserRepository;
import com.example.backend.entities.AppUser;
import com.example.backend.request.LoginRequest;
import com.example.backend.response.LoginResponse;
import com.example.backend.utility.JwtUtil;
// Spring ve güvenlik kütüphanelerini import eder
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

// HashMap ve Map veri tiplerini import eder
import java.util.HashMap;
import java.util.Map;

// Bu sınıf bir REST controller'dır
@RestController
// Tüm endpointler "/auth" ile başlar
@RequestMapping("/auth")
public class AuthController {

    // Kullanıcı veritabanı işlemleri için repository
    @Autowired
    private AppUserRepository appUserRepository;

    // JWT işlemleri için yardımcı sınıf
    @Autowired
    private JwtUtil jwtUtil;

    // Şifreleri hashlemek ve doğrulamak için encoder
    @Autowired
    private PasswordEncoder passwordEncoder;

    // Kullanıcı giriş işlemini gerçekleştiren endpoint
    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest loginRequest) {
        // İstekten kullanıcı adı ve şifreyi alır
        String username = loginRequest.getUsername();
        String password = loginRequest.getPassword();

        // Kullanıcıyı veritabanında bulur, yoksa hata fırlatır
        AppUser user = appUserRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

        // Şifre yanlışsa hata fırlatır
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Şifre yanlış");
        }

        // Kullanıcı adı ve rol ile JWT token üretir
        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        // Yanıt olarak token, rol ve kullanıcı adını döner
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setRole(user.getRole());
        response.setUsername(user.getUsername());
        return response;
    }
}