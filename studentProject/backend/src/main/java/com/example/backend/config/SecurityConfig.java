package com.example.backend.config; // Config paketi

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

@Configuration // Spring configuration anotasyonu
@EnableWebSecurity // Web güvenliği aktif et
public class SecurityConfig { // Güvenlik yapılandırma sınıfı

    @Autowired // Bağımlılık enjeksiyonu
    private CorsConfigurationSource corsConfigurationSource; // CORS yapılandırma kaynağı

    @Autowired // Bağımlılık enjeksiyonu
    private JwtAuthenticationFilter jwtAuthenticationFilter; // JWT kimlik doğrulama filtresi

    @Bean // Spring bean tanımı
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception { // Güvenlik filtre zinciri metodu
        http // HTTP güvenlik yapılandırması
            .csrf(csrf -> csrf.disable()) // CSRF korumasını devre dışı bırak
            .cors(cors -> cors.configurationSource(corsConfigurationSource)) // CORS yapılandırmasını uygula
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Oturum yönetimini stateless yap
            .authorizeHttpRequests(auth -> auth // Yetkilendirme kurallarını tanımla
                .requestMatchers("/auth/**", "/api/public", "/test/public", "/api/test/integration/**").permitAll() // Kimlik doğrulama gerektirmeyen endpointler
                .requestMatchers("/api/v3/students").hasAnyRole("ADMIN", "USER") // Öğrenci listeleme için ADMIN veya USER
                .requestMatchers("/api/v3/students/search**").hasAnyRole("ADMIN", "USER") // Öğrenci arama için ADMIN veya USER
                .requestMatchers("/api/v3/students/verified").hasAnyRole("ADMIN", "USER") // Onaylanmış öğrenciler için ADMIN veya USER
                .requestMatchers("/api/v3/students/verified/search**").hasAnyRole("ADMIN", "USER") // Onaylanmış öğrenci arama için ADMIN veya USER
                .requestMatchers("/api/v3/students/unverified").hasRole("ADMIN") // Onaylanmamış öğrenciler için sadece ADMIN
                .requestMatchers("/api/v3/students/*/approve").hasRole("ADMIN") // Öğrenci onaylama için sadece ADMIN
                .requestMatchers("/api/v3/students/*/visibility").hasRole("ADMIN") // Öğrenci görünürlük için sadece ADMIN
                .requestMatchers("/api/v3/students/**").hasRole("ADMIN") // Diğer öğrenci işlemleri için sadece ADMIN
                .requestMatchers("/api/v3/files/**").hasRole("ADMIN") // Dosya işlemleri için sadece ADMIN
                .anyRequest().authenticated() // Diğer tüm istekler için kimlik doğrulama gerekir
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class); // JWT filtresini ekle

        return http.build(); // Güvenlik filtre zincirini oluştur ve döndür
    }

    @Bean // Spring bean tanımı
    public PasswordEncoder passwordEncoder() { // Şifre encoder bean metodu
        return new BCryptPasswordEncoder(); // BCrypt encoder döndür
    }
}