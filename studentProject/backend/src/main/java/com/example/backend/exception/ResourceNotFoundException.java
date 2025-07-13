package com.example.backend.exception;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

// Kaynak bulunamadığında fırlatılan özel istisna (exception) sınıfı
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    // Hata mesajını ileten kurucu metod
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

