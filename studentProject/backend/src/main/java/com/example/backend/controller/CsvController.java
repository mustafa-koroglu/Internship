package com.example.backend.controller;

import com.example.backend.service.concretes.CsvProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/csv")
@RequiredArgsConstructor
@Slf4j
public class CsvController {

    private final CsvProcessingService csvProcessingService;

    /**
     * CSV dosyalarını manuel olarak işlemek için endpoint
     * @return İşlem sonucu
     */
    @PostMapping("/process")
    public ResponseEntity<Map<String, Object>> processCsvFiles() {
        log.info("Manuel CSV işleme isteği alındı");
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            csvProcessingService.processCsvFiles();
            response.put("success", true);
            response.put("message", "CSV dosyaları başarıyla işlendi");
            log.info("Manuel CSV işleme başarıyla tamamlandı");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Manuel CSV işleme sırasında hata: {}", e.getMessage(), e);
            response.put("success", false);
            response.put("message", "CSV işleme sırasında hata oluştu: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(response);
        }
    }
} 