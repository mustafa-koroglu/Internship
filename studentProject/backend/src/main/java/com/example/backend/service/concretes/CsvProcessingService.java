package com.example.backend.service.concretes; // Service paketi

import com.example.backend.dataAccess.FileRepository; // Dosya repository'si
import com.example.backend.entities.File; // Dosya entity'si
import lombok.RequiredArgsConstructor; // Constructor injection
import lombok.extern.slf4j.Slf4j; // Logging
import org.springframework.beans.factory.annotation.Value; // Konfigürasyon değeri
import org.springframework.stereotype.Service; // Service anotasyonu

import java.nio.file.Files; // Dosya işlemleri
import java.nio.file.Path; // Dosya yolu
import java.nio.file.Paths; // Dosya yolu oluşturucu
import java.util.concurrent.ExecutorService; // Thread executor
import java.util.concurrent.Executors; // Executor factory
import java.util.concurrent.CompletableFuture; // Asenkron işlem

@Service // Spring service anotasyonu
@RequiredArgsConstructor // Constructor injection
@Slf4j // Logging
public class CsvProcessingService { // CSV işleme servisi

    private final FileRepository fileRepository; // Dosya repository'si
    private final CsvFileProcessor csvFileProcessor; // CSV dosya işleyici

    @Value("${csv.watch.directory:./csv-files}") // CSV klasörü konfigürasyonu
    private String csvWatchDirectory; // CSV izleme klasörü

    private final ExecutorService csvProcessingExecutor = Executors.newSingleThreadExecutor(); // Tek thread executor

    public void shutdown() { // Uygulama kapatma metodu
        if (csvProcessingExecutor != null && !csvProcessingExecutor.isShutdown()) { // Executor kontrolü
            csvProcessingExecutor.shutdown(); // Executor'ı kapat
            log.info("CSV isleme thread pool kapatildi."); // Log mesajı
        }
    }

    public void processCsvFiles() { // CSV dosyalarını işleme metodu
        log.info("CSV dosyalari isleniyor..."); // Log mesajı
        
        try { // Hata yakalama bloğu
            Path directory = Paths.get(csvWatchDirectory); // Klasör yolunu oluştur
            if (!Files.exists(directory)) { // Klasör yoksa
                Files.createDirectories(directory); // Klasörü oluştur
                log.info("CSV klasoru olusturuldu: {}", csvWatchDirectory); // Log mesajı
                return; // Metodu sonlandır
            }

            java.io.File[] csvFiles = directory.toFile().listFiles((dir, name) -> // CSV dosyalarını filtrele
                name.toLowerCase().endsWith(".csv") && // CSV uzantılı
                !name.toLowerCase().endsWith(".done") && // Done olmayan
                !name.toLowerCase().endsWith(".fail")); // Fail olmayan

            if (csvFiles == null || csvFiles.length == 0) { // Dosya yoksa
                log.info("Islenecek CSV dosyasi bulunamadi."); // Log mesajı
                return; // Metodu sonlandır
            }

            processCsvFilesSequentially(csvFiles); // Dosyaları sırayla işle

        } catch (Exception e) { // Genel hata yakalama
            log.error("CSV dosyalari islenirken hata olustu: {}", e.getMessage(), e); // Hata log'u
        }
    }

    private void processCsvFilesSequentially(java.io.File[] csvFiles) { // Dosyaları sırayla işleme metodu
        log.info("{} CSV dosyasi multithread ile sirayla isleniyor...", csvFiles.length); // Log mesajı
        
        CompletableFuture<Void> previousTask = CompletableFuture.completedFuture(null); // Başlangıç task'ı
        
        for (java.io.File csvFile : csvFiles) { // Her dosya için
            final java.io.File currentFile = csvFile; // Dosyayı final yap
            
            previousTask = previousTask.thenRunAsync(() -> { // Asenkron zincir oluştur
                try { // Hata yakalama bloğu
                    log.info("CSV dosyasi islenmeye basliyor: {}", currentFile.getName()); // Başlangıç log'u
                    
                    // Dosyayı işle ve uzantısını hemen değiştir
                    processCsvFile(currentFile);
                    
                    log.info("CSV dosyasi isleme tamamlandi: {}", currentFile.getName()); // Bitiş log'u
                    
                } catch (Exception e) { // Hata yakalama
                    log.error("CSV dosyasi islenirken hata: {} - Dosya: {}", e.getMessage(), currentFile.getName(), e); // Hata log'u
                }
            }, csvProcessingExecutor); // Executor'da çalıştır
        }
        
        try { // Hata yakalama bloğu
            previousTask.get(); // Tüm task'ların bitmesini bekle
            log.info("Tum CSV dosyalari isleme tamamlandi."); // Tamamlanma log'u
        } catch (Exception e) { // Hata yakalama
            log.error("CSV dosyalari islenirken hata olustu: {}", e.getMessage(), e); // Hata log'u
        }
    }

    private void processCsvFile(java.io.File csvFile) { // Tek dosya işleme metodu
        log.info("Dosya islenmeye basliyor: {}", csvFile.getName()); // Başlangıç log'u
        
        CsvFileProcessor.CsvProcessingResult result = csvFileProcessor.processCsvFile(csvFile); // Dosyayı işle
        
        // Hemen uzantıyı değiştir
        if (result.isSuccess()) { // Başarılı ise
            log.info("Dosya basariyla islendi, uzanti degistiriliyor: {}", csvFile.getName()); // Başarı log'u
            renameFileToDone(csvFile); // Done olarak yeniden adlandır
            saveFileRecord(csvFile, true, result.getStudentCount(), result.getMessage()); // Başarı kaydı
        } else { // Başarısız ise
            log.warn("Dosya islenemedi, fail olarak isaretleniyor: {} - Hata: {}", csvFile.getName(), result.getErrorMessage()); // Hata log'u
            renameFileToFail(csvFile, result.getErrorMessage()); // Fail olarak yeniden adlandır
            saveFileRecord(csvFile, false, 0, result.getErrorMessage()); // Hata kaydı
        }
        
        log.info("Dosya isleme tamamlandi: {}", csvFile.getName()); // Bitiş log'u
        
        // 10 saniye bekle
        try {
            log.info("10 saniye bekleniyor..."); // Bekleme log'u
            Thread.sleep(10000); // 10 saniye bekle
            log.info("Bekleme tamamlandi, sonraki dosyaya geciliyor..."); // Bekleme bitiş log'u
        } catch (InterruptedException e) {
            log.warn("Bekleme kesildi: {}", e.getMessage()); // Kesilme log'u
            Thread.currentThread().interrupt(); // Thread'i kes
        }
    }

    private void renameFileToDone(java.io.File csvFile) { // Done yeniden adlandırma metodu
        try { // Hata yakalama bloğu
            String newName = csvFile.getName().replace(".csv", ".done"); // Yeni isim oluştur
            java.io.File doneFile = new java.io.File(csvFile.getParent(), newName); // Yeni dosya oluştur
            
            if (csvFile.renameTo(doneFile)) { // Yeniden adlandırma başarılı ise
                log.info("Dosya .done uzantisina cevrildi: {}", doneFile.getName()); // Başarı log'u
            } else { // Başarısız ise
                log.error("Dosya yeniden adlandirilamadi: {}", csvFile.getName()); // Hata log'u
            }
        } catch (Exception e) { // Hata yakalama
            log.error("Dosya yeniden adlandirilirken hata: {} - Dosya: {}", e.getMessage(), csvFile.getName()); // Hata log'u
        }
    }

    private void renameFileToFail(java.io.File csvFile, String errorMessage) { // Fail yeniden adlandırma metodu
        try { // Hata yakalama bloğu
            String newName = csvFile.getName().replace(".csv", ".fail"); // Yeni isim oluştur
            java.io.File failFile = new java.io.File(csvFile.getParent(), newName); // Yeni dosya oluştur
            
            if (csvFile.renameTo(failFile)) { // Yeniden adlandırma başarılı ise
                log.warn("Dosya .fail uzantisina cevrildi: {} - Hata: {}", failFile.getName(), errorMessage); // Uyarı log'u
            } else { // Başarısız ise
                log.error("Hatali dosya yeniden adlandirilamadi: {} - Hata: {}", csvFile.getName(), errorMessage); // Hata log'u
            }
        } catch (Exception e) { // Hata yakalama
            log.error("Hatali dosya yeniden adlandirilirken hata: {} - Dosya: {} - Orijinal Hata: {}", // Hata log'u
                     e.getMessage(), csvFile.getName(), errorMessage);
        }
    }

    private void saveFileRecord(java.io.File csvFile, boolean isSuccess, int studentCount, String description) { // Dosya kaydı metodu
        try { // Hata yakalama bloğu
            String fileName = csvFile.getName().replace(".csv", ""); // Dosya adını temizle
            String fullFileName = csvFile.getName(); // Tam dosya adı
            
            File fileRecord; // Dosya kaydı değişkeni
            if (isSuccess) { // Başarılı ise
                fileRecord = new File(fileName, fullFileName, studentCount, description); // Başarı kaydı oluştur
            } else { // Başarısız ise
                fileRecord = new File(fileName, fullFileName, description); // Hata kaydı oluştur
            }
            
            fileRepository.save(fileRecord); // Kaydı veritabanına kaydet
            log.info("Dosya kaydi veritabanina kaydedildi: {} - Durum: {}", fileName, isSuccess ? "DONE" : "FAIL"); // Başarı log'u
            
        } catch (Exception e) { // Hata yakalama
            log.error("Dosya kaydi veritabanina kaydedilirken hata: {} - Dosya: {}", e.getMessage(), csvFile.getName()); // Hata log'u
        }
    }
} 