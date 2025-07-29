package com.example.backend.scheduler;

import com.example.backend.service.concretes.CsvProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CsvProcessingScheduler {

    private final CsvProcessingService csvProcessingService;

    /**
     * Her 30 saniyede bir CSV dosyalarını işle (test için)
     * cron formatı: saniye dakika saat gün ay hafta
     * "0/30 * * * * *" = Her 30 saniyede bir
     * Normalde: "0 0 * * * *" = Her saatin başında (00:00, 01:00, 02:00, ...)
     */
    @Scheduled(cron = "0/30 * * * * *")
    public void processCsvFilesScheduled() {
        log.info("Scheduled CSV işleme job'ı başlatılıyor...");
        try {
            csvProcessingService.processCsvFiles();
            log.info("Scheduled CSV işleme job'ı tamamlandı.");
        } catch (Exception e) {
            log.error("Scheduled CSV işleme job'ında hata: {}", e.getMessage(), e);
        }
    }
} 