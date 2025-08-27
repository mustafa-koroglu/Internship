package com.example.backend.config;

import org.springframework.context.annotation.Bean; // Bean anotasyonu
import org.springframework.context.annotation.Configuration; // Configuration anotasyonu
import org.springframework.web.cors.CorsConfiguration; // CORS yapılandırması
import org.springframework.web.cors.CorsConfigurationSource; // CORS yapılandırma kaynağı
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // URL tabanlı CORS yapılandırma kaynağı

import java.util.Arrays; // Arrays sınıfı

@Configuration
public class CorsConfig {

    @Bean // Spring bean tanımı
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        // Tüm origin'lere izin ver (development için)
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        
        // İzin verilen HTTP metodları
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        
        // İzin verilen header'lar
        configuration.setAllowedHeaders(Arrays.asList(
            "Authorization", 
            "Content-Type", 
            "Accept", 
            "Origin", 
            "X-Requested-With",
            "Access-Control-Request-Method",
            "Access-Control-Request-Headers"
        ));
        
        // Exposed header'lar
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        
        // Credentials'a izin ver
        configuration.setAllowCredentials(true);
        
        // Preflight cache süresi (saniye)
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}