// Bu dosya, kaynak bulunamadığında fırlatılan özel istisna (exception) sınıfını tanımlar.
package com.example.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import java.io.Serial;

/**
 * Kaynak bulunamadığında (ör: öğrenci, kullanıcı) fırlatılan özel exception.
 * Bu exception fırlatıldığında HTTP 404 (NOT_FOUND) döner.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    // Java'nın Serializable arayüzü için benzersiz kimlik
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * Hata mesajını ileten kurucu metod
     * @param message Hata mesajı
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}