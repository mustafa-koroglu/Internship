package com.example.backend.exception; // Exception paketi

import org.springframework.http.HttpStatus; // HTTP durum kodu
import org.springframework.http.ResponseEntity; // HTTP yanıt wrapper'ı
import org.springframework.web.bind.annotation.ControllerAdvice; // Controller tavsiye anotasyonu
import org.springframework.web.bind.annotation.ExceptionHandler; // Exception handler anotasyonu
import org.springframework.web.bind.annotation.ResponseBody; // Yanıt gövdesi anotasyonu
import java.util.HashMap; // Hash map
import java.util.Map; // Map

@ControllerAdvice // Global controller tavsiye anotasyonu
public class GlobalExceptionHandler { // Global hata yöneticisi sınıfı

    @ExceptionHandler(Exception.class) // Tüm exception'ları yakala
    @ResponseBody // Yanıt gövdesi olarak döndür
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) { // Tüm exception'ları işleme metodu
        Map<String, String> error = new HashMap<>(); // Hata map'i oluştur
        error.put("message", ex.getMessage()); // Hata mesajını ekle
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error); // 400 yanıtı döndür
    }
} 