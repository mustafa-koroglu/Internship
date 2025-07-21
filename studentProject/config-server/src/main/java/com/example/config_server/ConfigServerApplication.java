// Bu dosya, Config Server uygulamasının ana başlangıç noktasıdır
package com.example.config_server;

// Spring Boot uygulamasını başlatmak için gerekli sınıflar import ediliyor
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// Spring Cloud Config Server'ı etkinleştiren anotasyon
import org.springframework.cloud.config.server.EnableConfigServer;

// Bu sınıf bir Spring Boot uygulamasıdır ve Config Server olarak çalışır
@SpringBootApplication
@EnableConfigServer
public class ConfigServerApplication {
    // Uygulamanın çalışmasını başlatan ana metod
    public static void main(String[] args) {
        SpringApplication.run(ConfigServerApplication.class, args);
    }
}