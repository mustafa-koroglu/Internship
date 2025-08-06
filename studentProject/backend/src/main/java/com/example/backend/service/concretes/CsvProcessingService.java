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
        if (csvProcessingExecutor != null && !csvProcessingExecutor.isShutdown()) { // Executor kontrolu
            csvProcessingExecutor.shutdown(); // Executor'i kapat
            log.info("CSV isleme thread pool kapatildi."); // Log mesaji
        }
    }

    public void processCsvFiles() { // CSV dosyalarini isleme metodu
        log.info("CSV dosyalari isleniyor..."); // Log mesaji
        
        try { // Hata yakalama bloğu
            Path directory = Paths.get(csvWatchDirectory); // Klasör yolunu oluştur
            if (!Files.exists(directory)) { // Klasor yoksa
                Files.createDirectories(directory); // Klasoru olustur
                log.info("CSV klasoru olusturuldu: {}", csvWatchDirectory); // Log mesaji
                return; // Metodu sonlandir
            }

            java.io.File[] csvFiles = directory.toFile().listFiles((dir, name) -> // CSV dosyalarını filtrele
                name.toLowerCase().endsWith(".csv") && // CSV uzantılı
                !name.toLowerCase().endsWith(".done") && // Done olmayan
                !name.toLowerCase().endsWith(".fail")); // Fail olmayan

            if (csvFiles == null || csvFiles.length == 0) { // Dosya yoksa
                log.info("Islenecek CSV dosyasi bulunamadi."); // Log mesaji
                return; // Metodu sonlandir
            }

            processCsvFilesSequentially(csvFiles); // Dosyaları sırayla işle

        } catch (Exception e) { // Genel hata yakalama
            log.error("CSV dosyalari islenirken hata olustu: {}", e.getMessage(), e); // Hata log'u
        }
    }

    private void processCsvFilesSequentially(java.io.File[] csvFiles) { // Dosyalari sirayla isleme metodu
        log.info("{} CSV dosyasi multithread ile sirayla isleniyor...", csvFiles.length); // Log mesaji
        
        CompletableFuture<Void> previousTask = CompletableFuture.completedFuture(null); // Başlangıç task'ı
        
        for (java.io.File csvFile : csvFiles) { // Her dosya için
            final java.io.File currentFile = csvFile; // Dosyayı final yap
            
            previousTask = previousTask.thenRunAsync(() -> { // Asenkron zincir oluştur
                try { // Hata yakalama blogu
                    log.info("CSV dosyasi islenmeye basliyor: {}", currentFile.getName()); // Baslangic log'u
                    
                    // Dosyayi isle ve uzantisini hemen degistir
                    processCsvFile(currentFile);
                    
                    log.info("CSV dosyasi isleme tamamlandi: {}", currentFile.getName()); // Bitis log'u
                    
                } catch (Exception e) { // Hata yakalama
                    log.error("CSV dosyasi islenirken hata: {} - Dosya: {}", e.getMessage(), currentFile.getName(), e); // Hata log'u
                }
            }, csvProcessingExecutor); // Executor'da çalıştır
        }
        
        try { // Hata yakalama blogu
            previousTask.get(); // Tum task'larin bitmesini bekle
            log.info("Tum CSV dosyalari isleme tamamlandi."); // Tamamlanma log'u
        } catch (Exception e) { // Hata yakalama
            log.error("CSV dosyalari islenirken hata olustu: {}", e.getMessage(), e); // Hata log'u
        }
    }

    private void processCsvFile(java.io.File csvFile) { // Tek dosya isleme metodu
        log.info("Dosya islenmeye basliyor: {}", csvFile.getName()); // Baslangic log'u
        
        try {
            CsvFileProcessor.CsvProcessingResult result = csvFileProcessor.processCsvFile(csvFile); // Dosyayi isle
            
            // Hemen uzantiyi degistir
            if (result.isSuccess()) { // Basarili ise
                log.info("Dosya basariyla islendi, uzanti degistiriliyor: {}", csvFile.getName()); // Basari log'u
                renameFileToDone(csvFile); // Done olarak yeniden adlandir
                saveFileRecord(csvFile, true, result.getStudentCount(), result.getMessage()); // Basari kaydi
            } else { // Basarisiz ise
                log.warn("Dosya islenemedi, fail olarak isaretleniyor: {} - Hata: {}", csvFile.getName(), result.getErrorMessage()); // Hata log'u
                renameFileToFail(csvFile, result.getErrorMessage()); // Fail olarak yeniden adlandir
                saveFileRecord(csvFile, false, 0, result.getErrorMessage()); // Hata kaydi
            }
        } catch (Exception e) {
            // Exception durumunda dosyayi fail olarak isaretle
            log.error("Dosya islenirken exception olustu: {} - Dosya: {}", e.getMessage(), csvFile.getName(), e);
            renameFileToFail(csvFile, "Exception: " + e.getMessage());
            saveFileRecord(csvFile, false, 0, "Exception: " + e.getMessage());
        }
        
        log.info("Dosya isleme tamamlandi: {}", csvFile.getName()); // Bitis log'u
        
        // 10 saniye bekle
        try {
            log.info("10 saniye bekleniyor..."); // Bekleme log'u
            Thread.sleep(10000); // 10 saniye bekle
            log.info("Bekleme tamamlandi, sonraki dosyaya geciliyor..."); // Bekleme bitis log'u
        } catch (InterruptedException e) {
            log.warn("Bekleme kesildi: {}", e.getMessage()); // Kesilme log'u
            Thread.currentThread().interrupt(); // Thread'i kes
        }
    }

    private void renameFileToDone(java.io.File csvFile) { // Done yeniden adlandırma metodu
        try { // Hata yakalama bloğu
            String newName = csvFile.getName().replace(".csv", ".done"); // Yeni isim oluştur
            java.io.File doneFile = new java.io.File(csvFile.getParent(), newName); // Yeni dosya oluştur
            
            // Dosya zaten .done uzantisina sahipse, islem yapma
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
                log.info("Dosya .done uzantisina cevrildi: {}", doneFile.getName()); // Basari log'u
            } else { // Basarisiz ise
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
            
            if (csvFile.renameTo(failFile)) { // Yeniden adlandirma basarili ise
                log.warn("Dosya .fail uzantisina cevrildi: {} - Hata: {}", failFile.getName(), errorMessage); // Uyari log'u
            } else { // Basarisiz ise
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
            
            fileRepository.save(fileRecord); // Kaydi veritabanina kaydet
            log.info("Dosya kaydi veritabanina kaydedildi: {} - Durum: {}", fileName, isSuccess ? "DONE" : "FAIL"); // Basari log'u
            
        } catch (Exception e) { // Hata yakalama
            log.error("Dosya kaydi veritabanina kaydedilirken hata: {} - Dosya: {}", e.getMessage(), csvFile.getName()); // Hata log'u
        }
    }
} 