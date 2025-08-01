package com.example.backend; // Ana paket tanımı

import org.springframework.boot.SpringApplication; // Spring Boot uygulama başlatıcı
import org.springframework.boot.autoconfigure.SpringBootApplication; // Spring Boot otomatik konfigürasyon
import org.springframework.scheduling.annotation.EnableScheduling; // Zamanlanmış görevler için

@SpringBootApplication // Spring Boot ana uygulama anotasyonu
@EnableScheduling // Zamanlanmış görevleri aktif et
public class BackendApplication { // Ana uygulama sınıfı

    public static void main(String[] args) { // Uygulama başlangıç metodu
        SpringApplication.run(BackendApplication.class, args); // Spring Boot uygulamasını başlat
    }
}
