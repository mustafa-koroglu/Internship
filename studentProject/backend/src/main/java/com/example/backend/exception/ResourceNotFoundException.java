// Bu dosya, kaynak bulunamadığında fırlatılan özel istisna (exception) sınıfını tanımlar.
package com.example.backend.exception;

// HTTP durum kodu ve exception mapping için gerekli kütüphaneleri import eder
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

// Bu exception fırlatıldığında HTTP 404 (NOT_FOUND) döndürülmesini sağlar
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Java'nın Serializable arayüzü için benzersiz kimlik
    @Serial
    private static final long serialVersionUID = 1L;

    // Hata mesajını ileten kurucu metod
    public ResourceNotFoundException(String message) {
        super(message);
    }
}