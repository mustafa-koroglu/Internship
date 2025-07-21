// Bu dosya, uygulamanın güvenlik yapılandırmasını (Spring Security) tanımlar.
package com.example.backend.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfigurationSource;
import com.example.backend.filter.JwtAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Uygulamanın Spring Security güvenlik yapılandırmasını tanımlar.
 * JWT tabanlı kimlik doğrulama, CORS ve endpoint yetkilendirme kuralları burada belirlenir.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** CORS yapılandırmasını sağlayan bean */
    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    /** JWT doğrulama filtresi */
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Güvenlik filtre zincirini tanımlar ve yapılandırır
     * @param http HttpSecurity nesnesi
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF korumasını devre dışı bırakır (stateless API için)
            .csrf(csrf -> csrf.disable())
            // CORS yapılandırmasını uygular
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            // Oturum yönetimini stateless olarak ayarlar (her istekte kimlik doğrulama gerekir)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // Yetkilendirme kurallarını tanımlar
            .authorizeHttpRequests(auth -> auth
                // Kimlik doğrulama gerektirmeyen endpointler
                .requestMatchers("/auth/**", "/api/public", "/test/public").permitAll()
                // Öğrenci listeleme ve arama endpointleri için ADMIN veya USER rolü gerekir
                .requestMatchers("/api/v3/students").hasAnyRole("ADMIN", "USER")
                .requestMatchers("/api/v3/students/search**").hasAnyRole("ADMIN", "USER")
                // Öğrenci ekleme, silme, güncelleme için sadece ADMIN rolü gerekir
                .requestMatchers("/api/v3/students/**").hasRole("ADMIN")
                // Diğer tüm istekler için kimlik doğrulama gerekir
                .anyRequest().authenticated()
            )
            // JWT doğrulama filtresini, UsernamePasswordAuthenticationFilter'dan önce ekler
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Yapılandırılmış SecurityFilterChain'i döner
        return http.build();
    }

    /**
     * Şifreleri hashlemek için BCryptPasswordEncoder bean'i tanımlar
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}