package com.example.backend.scheduler; // Scheduler paketi

import com.example.backend.service.concretes.CsvProcessingService; // CSV işleme servisi
import lombok.RequiredArgsConstructor; // Constructor injection
import lombok.extern.slf4j.Slf4j; // Logging
import org.springframework.scheduling.annotation.Scheduled; // Zamanlanmış görev anotasyonu
import org.springframework.stereotype.Component; // Component anotasyonu

@Component // Spring component anotasyonu
@RequiredArgsConstructor // Constructor injection
@Slf4j // Logging
public class CsvProcessingScheduler { // CSV işleme zamanlayıcı sınıfı

    private final CsvProcessingService csvProcessingService; // CSV işleme servisi

    @Scheduled(cron = "0/30 * * * * *") // Her 30 saniyede bir çalıştır
    public void processCsvFilesScheduled() { // Zamanlanmış CSV işleme metodu
        log.info("Scheduled CSV isleme job'i baslatiliyor..."); // Başlangıç log'u
        try { // Hata yakalama bloğu
            csvProcessingService.processCsvFiles(); // CSV dosyalarını işle
            log.info("Scheduled CSV isleme job'i tamamlandi."); // Tamamlanma log'u
        } catch (Exception e) { // Hata yakalama
            log.error("Scheduled CSV isleme job'inda hata: {}", e.getMessage(), e); // Hata log'u
        }
    }
} 