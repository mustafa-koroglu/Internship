package com.example.backend.config;

import org.springframework.beans.factory.annotation.Autowired; // Autowired anotasyonu
import org.springframework.context.annotation.Bean; // Bean anotasyonu
import org.springframework.context.annotation.Configuration; // Configuration anotasyonu
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // HTTP güvenlik yapılandırması
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Web güvenlik aktif etme
import org.springframework.security.web.SecurityFilterChain; // Güvenlik filtre zinciri
import org.springframework.web.cors.CorsConfigurationSource; // CORS yapılandırma kaynağı
import com.example.backend.filter.JwtAuthenticationFilter; // JWT kimlik doğrulama filtresi
import org.springframework.security.config.http.SessionCreationPolicy; // Oturum oluşturma politikası
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Kullanıcı adı şifre kimlik doğrulama filtresi
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // BCrypt şifre encoder'ı
import org.springframework.security.crypto.password.PasswordEncoder; // Şifre encoder arayüzü

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**", "/api/public", "/test/public", "/api/test/integration/**").permitAll()
                        .requestMatchers("/api/v1/teachers/**").permitAll()
                        .requestMatchers("/api/v1/ip-addresses/**").hasRole("ADMIN")
                        .requestMatchers("/api/lessons/**").hasRole("ADMIN")
                        .requestMatchers("/api/v3/students").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/v3/students/search**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/v3/students/verified").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/v3/students/verified/search**").hasAnyRole("ADMIN", "USER")
                        .requestMatchers("/api/v3/students/unverified").hasRole("ADMIN")
                        .requestMatchers("/api/v3/students/*/approve").hasRole("ADMIN")
                        .requestMatchers("/api/v3/students/*/visibility").hasRole("ADMIN")
                        .requestMatchers("/api/v3/students/**").hasRole("ADMIN")
                        .requestMatchers("/api/v3/files/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}