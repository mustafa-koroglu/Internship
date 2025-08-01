package com.example.backend.controller; // Controller paketi

import com.example.backend.service.concretes.CsvProcessingService; // CSV işleme servisi
import lombok.RequiredArgsConstructor; // Constructor injection
import lombok.extern.slf4j.Slf4j; // Logging
import org.springframework.http.ResponseEntity; // HTTP yanıt wrapper'ı
import org.springframework.web.bind.annotation.PostMapping; // POST mapping anotasyonu
import org.springframework.web.bind.annotation.RequestMapping; // Request mapping anotasyonu
import org.springframework.web.bind.annotation.RestController; // REST controller anotasyonu

import java.util.HashMap; // Hash map
import java.util.Map; // Map

@RestController // REST controller anotasyonu
@RequestMapping("/api/csv") // CSV endpoint prefix'i
@RequiredArgsConstructor // Constructor injection
@Slf4j // Logging
public class CsvController { // CSV controller sınıfı

    private final CsvProcessingService csvProcessingService; // CSV işleme servisi

    @PostMapping("/process") // CSV işleme endpoint'i
    public ResponseEntity<Map<String, Object>> processCsvFiles() { // CSV dosyalarını işleme metodu
        log.info("Manuel CSV isleme istegi alindi"); // Log mesajı
        
        Map<String, Object> response = new HashMap<>(); // Yanıt map'i oluştur
        
        try { // Hata yakalama bloğu
            csvProcessingService.processCsvFiles(); // CSV dosyalarını işle
            response.put("success", true); // Başarı durumunu ekle
            response.put("message", "CSV dosyalari basariyla islendi"); // Başarı mesajını ekle
            log.info("Manuel CSV isleme basariyla tamamlandi"); // Başarı log'u
            
            return ResponseEntity.ok(response); // Başarılı yanıt döndür
            
        } catch (Exception e) { // Hata yakalama
            log.error("Manuel CSV isleme sirasinda hata: {}", e.getMessage(), e); // Hata log'u
            response.put("success", false); // Başarısızlık durumunu ekle
            response.put("message", "CSV isleme sirasinda hata olustu: " + e.getMessage()); // Hata mesajını ekle
            
            return ResponseEntity.internalServerError().body(response); // Hata yanıtı döndür
        }
    }
} 