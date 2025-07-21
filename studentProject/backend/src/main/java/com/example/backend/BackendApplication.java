// Bu dosya, backend uygulamasının ana giriş noktasıdır.
package com.example.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Backend Spring Boot uygulamasının başlangıç noktası.
 * @SpringBootApplication anotasyonu ile ana uygulama olarak işaretlenir.
 */
@SpringBootApplication
public class BackendApplication {

    /**
     * Uygulamanın çalışmasını başlatan ana metod.
     * @param args Komut satırı argümanları
     */
    public static void main(String[] args) {
        // Spring Boot uygulamasını başlatır
        SpringApplication.run(BackendApplication.class, args);
    }
}
