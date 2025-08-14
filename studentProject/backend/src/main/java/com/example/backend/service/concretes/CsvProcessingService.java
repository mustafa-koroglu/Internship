package com.example.backend.service.concretes;

import com.example.backend.dataAccess.FileRepository;
import com.example.backend.entities.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class CsvProcessingService { // CSV işleme servisi

    private final FileRepository fileRepository;
    private final CsvFileProcessor csvFileProcessor;

    @Value("${csv.watch.directory:./csv-files}")
    private String csvWatchDirectory;

    private final ExecutorService csvProcessingExecutor = Executors.newVirtualThreadPerTaskExecutor(); // Virtual thread executor

    public void shutdown() {
        if (csvProcessingExecutor != null && !csvProcessingExecutor.isShutdown()) { // Executor kontrolu
            csvProcessingExecutor.shutdown();
            log.info("CSV isleme thread pool kapatildi.");
        }
    }

    public void processCsvFiles() { // CSV dosyalarini isleme metodu
        log.info("CSV dosyalari isleniyor...");

        try {
            // csv dosyalarını path çevirme
            Path directory = Paths.get(csvWatchDirectory);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                log.info("CSV klasoru olusturuldu: {}", csvWatchDirectory);
                return;
            }

            // CSV dosyalarını filtreleme
            java.io.File[] csvFiles = directory.toFile().listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".csv") &&
                            !name.toLowerCase().endsWith(".done") &&
                            !name.toLowerCase().endsWith(".fail"));

            if (csvFiles == null || csvFiles.length == 0) {
                log.info("Islenecek CSV dosyasi bulunamadi.");
                return;
            }

            processCsvFilesSequentially(csvFiles); // Dosyaları sırayla işle

        } catch (Exception e) {
            log.error("CSV dosyalari islenirken hata olustu: {}", e.getMessage(), e);
        }
    }

    // dosya işleme methodu (asenkron)
    private void processCsvFilesSequentially(java.io.File[] csvFiles) {
        log.info("{} CSV dosyasi multithread ile sirayla isleniyor...", csvFiles.length);

        CompletableFuture<Void> previousTask = CompletableFuture.completedFuture(null); // Başlangıç task'ı

        for (java.io.File csvFile : csvFiles) {
            final java.io.File currentFile = csvFile;

            previousTask = previousTask.thenRunAsync(() -> { // Asenkron zincir oluştur
                try {
                    log.info("CSV dosyasi islenmeye basliyor: {}", currentFile.getName());

                    // Dosyayi isle ve uzantisini hemen degistir
                    processCsvFile(currentFile);

                    log.info("CSV dosyasi isleme tamamlandi: {}", currentFile.getName());

                } catch (Exception e) {
                    log.error("CSV dosyasi islenirken hata: {} - Dosya: {}", e.getMessage(), currentFile.getName(), e);
                }
            }, csvProcessingExecutor);
        }

        try {
            previousTask.get();
            log.info("Tum CSV dosyalari isleme tamamlandi.");
        } catch (Exception e) {
            log.error("CSV dosyalari islenirken hata olustu: {}", e.getMessage(), e);
        }
    }

    private void processCsvFile(java.io.File csvFile) {
        log.info("Dosya islenmeye basliyor: {}", csvFile.getName());

        try {
            CsvFileProcessor.CsvProcessingResult result = csvFileProcessor.processCsvFile(csvFile);

            // Hemen uzantiyi degistir
            if (result.isSuccess()) {
                log.info("Dosya basariyla islendi, uzanti degistiriliyor: {}", csvFile.getName());
                renameFileToDone(csvFile);
                saveFileRecord(csvFile, true, result.getStudentCount(), result.getMessage());
            } else { // Basarisiz ise
                log.warn("Dosya islenemedi, fail olarak isaretleniyor: {} - Hata: {}", csvFile.getName(), result.getErrorMessage());
                renameFileToFail(csvFile, result.getErrorMessage());
                saveFileRecord(csvFile, false, 0, result.getErrorMessage());
            }
        } catch (Exception e) {
            // Exception durumunda dosyayi fail olarak isaretle
            log.error("Dosya islenirken exception olustu: {} - Dosya: {}", e.getMessage(), csvFile.getName(), e);
            renameFileToFail(csvFile, "Exception: " + e.getMessage());
            saveFileRecord(csvFile, false, 0, "Exception: " + e.getMessage());
        }

        log.info("Dosya isleme tamamlandi: {}", csvFile.getName());


        try {
            log.info("10 saniye bekleniyor...");
            // 10 saniye bekle
            Thread.sleep(Duration.ofSeconds(10).toMillis());
            log.info("Bekleme tamamlandi, sonraki dosyaya geciliyor...");
        } catch (InterruptedException e) {
            log.warn("Bekleme kesildi: {}", e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

    private void renameFileToDone(java.io.File csvFile) { // Done yeniden adlandırma metodu
        try {
            String newName = csvFile.getName().replace(".csv", ".done");
            java.io.File doneFile = new java.io.File(csvFile.getParent(), newName);


            if (csvFile.getName().endsWith(".done")) {
                log.info("Dosya zaten .done uzantisina sahip: {}", csvFile.getName());
                return;
            }

            // Hedef dosya zaten varsa, once sil
            if (doneFile.exists()) {
                if (doneFile.delete()) {
                    log.info("Eski .done dosyasi silindi: {}", doneFile.getName());
                } else {
                    log.warn("Eski .done dosyasi silinemedi: {}", doneFile.getName());
                }
            }

            if (csvFile.renameTo(doneFile)) { // Yeniden adlandirma basarili ise
                log.info("Dosya .done uzantisina cevrildi: {}", doneFile.getName());
            } else { // Basarisiz ise
                log.error("Dosya yeniden adlandirilamadi: {}", csvFile.getName());
            }
        } catch (Exception e) {
            log.error("Dosya yeniden adlandirilirken hata: {} - Dosya: {}", e.getMessage(), csvFile.getName());
        }
    }

    private void renameFileToFail(java.io.File csvFile, String errorMessage) { // Fail yeniden adlandırma metodu
        try {
            String newName = csvFile.getName().replace(".csv", ".fail");
            java.io.File failFile = new java.io.File(csvFile.getParent(), newName);

            if (csvFile.renameTo(failFile)) { // Yeniden adlandirma basarili ise
                log.warn("Dosya .fail uzantisina cevrildi: {} - Hata: {}", failFile.getName(), errorMessage);
            } else {
                log.error("Hatali dosya yeniden adlandirilamadi: {} - Hata: {}", csvFile.getName(), errorMessage);
            }
        } catch (Exception e) { // Hata yakalama
            log.error("Hatali dosya yeniden adlandirilirken hata: {} - Dosya: {} - Orijinal Hata: {}",
                    e.getMessage(), csvFile.getName(), errorMessage);
        }
    }

    private void saveFileRecord(java.io.File csvFile, boolean isSuccess, int studentCount, String description) { // Dosya kaydı metodu
        try {
            String fileName = csvFile.getName().replace(".csv", ""); // Dosya adını temizle
            String fullFileName = csvFile.getName();

            File fileRecord; // Dosya kaydı değişkeni
            if (isSuccess) {
                fileRecord = new File(fileName, fullFileName, studentCount, description);
            } else {
                fileRecord = new File(fileName, fullFileName, description);
            }

            fileRepository.save(fileRecord); // Kaydi veritabanina kaydet
            log.info("Dosya kaydi veritabanina kaydedildi: {} - Durum: {}", fileName, isSuccess ? "DONE" : "FAIL");

        } catch (Exception e) { // Hata yakalama
            log.error("Dosya kaydi veritabanina kaydedilirken hata: {} - Dosya: {}", e.getMessage(), csvFile.getName());
        }
    }
} 