package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import java.util.HashMap;
import java.util.Map;

/**
 * Uygulama genelinde oluşan tüm Exception'ları yakalayan global hata yöneticisi.
 * Hataları JSON formatında frontend'e iletir.
 */
@ControllerAdvice
public class GlobalExceptionHandler {
    /**
     * Tüm Exception'ları yakalar ve hata mesajını JSON olarak döner.
     * @param ex Fırlatılan exception
     * @return Hata mesajı içeren JSON response
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex) {
        Map<String, String> error = new HashMap<>();
        error.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
} 