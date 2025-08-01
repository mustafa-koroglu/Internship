package com.example.backend.filter; // Filter paketi

import com.example.backend.service.concretes.AppUserDetailsService; // Kullanıcı detay servisi
import com.example.backend.utility.JwtUtil; // JWT utility
import jakarta.servlet.FilterChain; // Filtre zinciri
import jakarta.servlet.ServletException; // Servlet hatası
import jakarta.servlet.http.HttpServletRequest; // HTTP istek
import jakarta.servlet.http.HttpServletResponse; // HTTP yanıt
import org.springframework.beans.factory.annotation.Autowired; // Autowired anotasyonu
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Kullanıcı adı şifre kimlik doğrulama token'ı
import org.springframework.security.core.context.SecurityContextHolder; // Güvenlik bağlam tutucusu
import org.springframework.security.core.userdetails.UserDetails; // Kullanıcı detayları
import org.springframework.stereotype.Component; // Component anotasyonu
import org.springframework.web.filter.OncePerRequestFilter; // Her istek için bir kez çalışan filtre
import java.io.IOException; // IO hatası

@Component // Spring component anotasyonu
public class JwtAuthenticationFilter extends OncePerRequestFilter { // JWT kimlik doğrulama filtresi

    @Autowired // Bağımlılık enjeksiyonu
    private JwtUtil jwtUtil; // JWT utility

    @Autowired // Bağımlılık enjeksiyonu
    private AppUserDetailsService userDetailsService; // Kullanıcı detay servisi

    @Override // Override anotasyonu
    @SuppressWarnings("null") // Null uyarısını bastır
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) // Filtre iç metodu
            throws ServletException, IOException { // Servlet ve IO hatası

        final String authHeader = request.getHeader("Authorization"); // Authorization başlığını al
        String username = null; // Kullanıcı adı değişkeni
        String jwt = null; // JWT değişkeni

        if (authHeader != null && authHeader.startsWith("Bearer ")) { // Authorization başlığı Bearer ile başlıyorsa
            jwt = authHeader.substring(7); // JWT'yi çıkar
            username = jwtUtil.extractUsername(jwt); // Kullanıcı adını çıkar
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) { // Kullanıcı adı varsa ve güvenlik bağlamında kimlik doğrulama yoksa
            UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Kullanıcı detaylarını yükle

            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) { // Token geçerliyse
                UsernamePasswordAuthenticationToken authToken = // Kimlik doğrulama token'ı oluştur
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken); // Güvenlik bağlamına ekle
            }
        }
        filterChain.doFilter(request, response); // Filtre zincirine devam et
    }
}