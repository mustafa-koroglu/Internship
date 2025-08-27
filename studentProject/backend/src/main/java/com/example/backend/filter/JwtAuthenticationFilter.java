package com.example.backend.filter;

import com.example.backend.service.concretes.AppUserDetailsService; // Kullanıcı detay servisi
import com.example.backend.utility.JwtUtil; // JWT utility
import jakarta.servlet.FilterChain; // Filtre zinciri
import jakarta.servlet.ServletException; // Servlet hatası
import jakarta.servlet.http.HttpServletRequest; // HTTP istek
import jakarta.servlet.http.HttpServletResponse; // HTTP yanıt
import org.springframework.beans.factory.annotation.Autowired; // Autowired anotasyonu
import org.springframework.lang.NonNull; // NonNull anotasyonu
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Kullanıcı adı şifre kimlik doğrulama token'ı
import org.springframework.security.core.context.SecurityContextHolder; // Güvenlik bağlam tutucusu
import org.springframework.security.core.userdetails.UserDetails; // Kullanıcı detayları
import org.springframework.stereotype.Component; // Component anotasyonu
import org.springframework.web.filter.OncePerRequestFilter; // Her istek için bir kez çalışan filtre

import java.io.IOException; // IO hatası

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AppUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}