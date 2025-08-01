package com.example.backend.config; // Config paketi

import org.springframework.context.annotation.Bean; // Bean anotasyonu
import org.springframework.context.annotation.Configuration; // Configuration anotasyonu
import org.springframework.web.cors.CorsConfiguration; // CORS yapılandırması
import org.springframework.web.cors.CorsConfigurationSource; // CORS yapılandırma kaynağı
import org.springframework.web.cors.UrlBasedCorsConfigurationSource; // URL tabanlı CORS yapılandırma kaynağı
import java.util.Arrays; // Arrays sınıfı

@Configuration // Spring configuration anotasyonu
public class CorsConfig { // CORS yapılandırma sınıfı

    @Bean // Spring bean tanımı
    public CorsConfigurationSource corsConfigurationSource() { // CORS yapılandırma kaynağı metodu
        CorsConfiguration configuration = new CorsConfiguration(); // Yeni CORS yapılandırması oluştur
        configuration.setAllowedOriginPatterns(Arrays.asList("*")); // Tüm origin'lere izin ver
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // İzin verilen HTTP metodlarını belirt
        configuration.setAllowedHeaders(Arrays.asList("*")); // Tüm header'lara izin ver
        configuration.setAllowCredentials(true); // Kimlik bilgisi iletilmesine izin ver
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource(); // URL tabanlı CORS kaynağı oluştur
        source.registerCorsConfiguration("/**", configuration); // CORS yapılandırmasını tüm path'lere uygula
        return source; // Yapılandırmayı döndür
    }
}