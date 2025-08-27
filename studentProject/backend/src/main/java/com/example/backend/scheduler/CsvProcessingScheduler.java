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

    @Scheduled(cron = "0/30 * * * * *") // Her 30 saniyede bir çalıştır
    public void processCsvFilesScheduled() {
        log.info("Scheduled CSV isleme job'i baslatiliyor...");
        try {
            csvProcessingService.processCsvFiles();
            log.info("Scheduled CSV isleme job'i tamamlandi.");
        } catch (Exception e) {
            log.error("Scheduled CSV isleme job'inda hata: {}", e.getMessage(), e);
        }
    }
} 