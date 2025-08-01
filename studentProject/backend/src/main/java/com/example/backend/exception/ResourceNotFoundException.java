package com.example.backend.exception; // Exception paketi

import org.springframework.http.HttpStatus; // HTTP durum kodu
import org.springframework.web.bind.annotation.ResponseStatus; // Yanıt durumu anotasyonu
import java.io.Serial; // Serial anotasyonu

@ResponseStatus(value = HttpStatus.NOT_FOUND) // HTTP 404 yanıt durumu
public class ResourceNotFoundException extends RuntimeException { // Kaynak bulunamadı hatası
    @Serial // Serial anotasyonu
    private static final long serialVersionUID = 1L; // Serial versiyon ID'si

    public ResourceNotFoundException(String message) { // Kurucu metod
        super(message); // Üst sınıf kurucusunu çağır
    }
}