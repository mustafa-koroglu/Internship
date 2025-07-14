// Bu dosya, gelen HTTP isteklerinde JWT doğrulaması yapan filtreyi tanımlar.
package com.example.backend.filter;

// Kullanıcı detay servisi ve JWT yardımcı sınıfını import eder
import com.example.backend.service.concretes.AppUserDetailsService;
import com.example.backend.utility.JwtUtil;
// Servlet ve filtre işlemleri için gerekli kütüphaneleri import eder
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
// Spring Security ve Spring bileşenlerini import eder
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// Bu sınıf bir Spring bileşenidir ve her istekte bir kez çalışır
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // JWT işlemleri için yardımcı sınıf
    @Autowired
    private JwtUtil jwtUtil;

    // Kullanıcı detaylarını yüklemek için servis
    @Autowired
    private AppUserDetailsService userDetailsService;

    // Her HTTP isteğinde çalışacak filtre metodu
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Authorization başlığını alır
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // Authorization başlığı "Bearer " ile başlıyorsa JWT'yi çıkarır ve kullanıcı adını alır
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            jwt = authHeader.substring(7);
            username = jwtUtil.extractUsername(jwt);
        }

        // Kullanıcı adı varsa ve güvenlik bağlamında kimlik doğrulama yoksa devam eder
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Kullanıcı detaylarını yükler
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Token geçerliyse, kullanıcıyı güvenlik bağlamına ekler
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // Filtre zincirine devam eder
        filterChain.doFilter(request, response);
    }
}