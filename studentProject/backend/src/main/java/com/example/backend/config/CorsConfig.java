// Bu dosya, uygulamanın CORS (Cross-Origin Resource Sharing) ayarlarını yapılandırır.
package com.example.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

/**
 * Uygulamanın CORS (Cross-Origin Resource Sharing) ayarlarını yapılandıran konfigürasyon sınıfı.
 * Tüm origin'lere, header'lara ve HTTP metodlarına izin verir.
 */
@Configuration
public class CorsConfig {

    /**
     * CORS yapılandırmasını sağlayan bean metodu
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        // Yeni bir CORS yapılandırma nesnesi oluşturur
        CorsConfiguration configuration = new CorsConfiguration();
        // Tüm origin'lere izin verir
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        // İzin verilen HTTP metodlarını belirtir
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        // Tüm header'lara izin verir
        configuration.setAllowedHeaders(Arrays.asList("*"));
        // Kimlik bilgisi (cookie, authorization header vs.) iletilmesine izin verir
        configuration.setAllowCredentials(true);
        // CORS yapılandırmasını belirli path'lere uygular
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        // Yapılandırmayı döner
        return source;
    }
}