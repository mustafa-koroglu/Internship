package com.example.backend.service.concretes; // Service paketi

import com.opencsv.CSVReader; // CSV okuyucu
import com.opencsv.exceptions.CsvValidationException; // CSV doğrulama hatası
import lombok.RequiredArgsConstructor; // Constructor injection
import lombok.extern.slf4j.Slf4j; // Logging
import org.springframework.integration.support.MessageBuilder; // Message builder
import org.springframework.messaging.MessageChannel; // Message channel
import org.springframework.stereotype.Component; // Component anotasyonu

import java.io.File; // Dosya sınıfı
import java.io.FileReader; // Dosya okuyucu
import java.io.IOException; // IO hatası
import java.util.ArrayList; // ArrayList
import java.util.List; // Liste

@Component // Spring component anotasyonu
@RequiredArgsConstructor // Constructor injection
@Slf4j // Logging
public class CsvFileProcessor { // CSV dosya işleyici sınıfı

    private final MessageChannel csvLineChannel; // CSV satır işleme kanalı

    public CsvProcessingResult processCsvFile(File csvFile) { // CSV dosyasini isleme metodu
        log.info("CSV dosyasi isleniyor: {}", csvFile.getName()); // Log mesaji

        CsvProcessingResult result = new CsvProcessingResult(); // Sonuç nesnesi oluştur
        CSVReader reader = null; // CSV okuyucu değişkeni

        try { // Hata yakalama bloğu
            reader = new CSVReader(new FileReader(csvFile)); // CSV okuyucu oluştur

            String[] header = reader.readNext(); // Header'ı oku
            if (header == null) { // Header yoksa
                result.setSuccess(false); // Basarisiz olarak isaretle
                result.setErrorMessage("Dosya bos"); // Hata mesaji
                return result; // Sonucu dondur
            }

            if (!isValidHeader(header)) { // Header gecersizse
                result.setSuccess(false); // Basarisiz olarak isaretle
                result.setErrorMessage("Gecersiz header formati - Beklenen: name,surname,number"); // Hata mesaji
                return result; // Sonucu dondur
            }

            List<String[]> csvLines = new ArrayList<>(); // CSV satırları listesi oluştur
            String[] line; // Satır değişkeni
            int lineNumber = 1; // Satır numarası

            while ((line = reader.readNext()) != null) { // Her satır için
                lineNumber++; // Satır numarasını artır
                try { // Hata yakalama bloğu
                    // Satırı doğrudan listeye ekle (transformer'da işlenecek)
                    csvLines.add(line); // Listeye ekle
                } catch (Exception e) { // Hata yakalama
                    log.error("Satir {} islenirken hata: {}", lineNumber, e.getMessage()); // Hata log'u
                    result.setSuccess(false); // Basarisiz olarak isaretle
                    result.setErrorMessage("Satir " + lineNumber + " islenirken hata: " + e.getMessage()); // Hata mesaji
                    return result; // Sonucu dondur
                }
            }

            if (!csvLines.isEmpty()) { // CSV satırları listesi bos degilse
                // CSV satirlarini integration channel'a gonder
                sendCsvLinesToChannel(csvLines); // Integration channel'a gonder
                result.setSuccess(true); // Basarili olarak isaretle
                result.setStudentCount(csvLines.size()); // CSV satır sayısını ayarla
                result.setMessage(csvLines.size() + " CSV satiri integration channel'a gonderildi"); // Basari mesaji
            } else { // CSV satırları listesi bossa
                result.setSuccess(false); // Basarisiz olarak isaretle
                result.setErrorMessage("CSV dosyasindan hic gecerli satir okunamadi"); // Hata mesaji
            }

        } catch (IOException | CsvValidationException e) { // IO veya CSV hatasi
            log.error("CSV dosyasi okunurken hata: {}", e.getMessage()); // Hata log'u
            result.setSuccess(false); // Basarisiz olarak isaretle
            result.setErrorMessage("Dosya okuma hatasi: " + e.getMessage()); // Hata mesaji
        } finally { // Son islem blogu
            if (reader != null) { // Okuyucu varsa
                try { // Hata yakalama blogu
                    reader.close(); // Okuyucuyu kapat
                } catch (IOException e) { // IO hatasi
                    log.error("CSV reader kapatilirken hata: {}", e.getMessage()); // Hata log'u
                }
            }
        }

        return result; // Sonucu döndür
    }

    private boolean isValidHeader(String[] header) { // Header doğrulama metodu
        if (header == null || header.length < 3) { // Header null veya kısa ise
            return false; // Geçersiz
        }

        boolean hasName = false, hasSurname = false, hasNumber = false; // Gerekli alanlar

        for (String column : header) { // Her sütun için
            String columnLower = column.toLowerCase().trim(); // Sütun adını küçük harfe çevir

            if (columnLower.equals("name")) { // İsim alanı
                hasName = true; // İsim var
            } else if (columnLower.equals("surname")) { // Soyisim alanı
                hasSurname = true; // Soyisim var
            } else if (columnLower.equals("number")) { // Numara alanı
                hasNumber = true; // Numara var
            }
        }

        return hasName && hasSurname && hasNumber; // Tüm alanlar varsa geçerli
    }

    private void sendCsvLinesToChannel(List<String[]> csvLines) { // CSV satirlarini channel'a gonderme metodu
        try { // Hata yakalama blogu
            log.info("{} CSV satiri integration channel'a gonderiliyor...", csvLines.size()); // Log mesaji

            // Her CSV satirini ayri ayri channel'a gonder
            for (String[] csvLine : csvLines) {
                log.info("CSV satiri channel'a gonderiliyor: {}", String.join(",", csvLine));

                // Her CSV satirini ayri message olarak gonder
                csvLineChannel.send(MessageBuilder.withPayload(csvLine).build());

                log.info("CSV satiri basariyla channel'a gonderildi");
            }

            log.info("Tum CSV satirlari basariyla integration channel'a gonderildi"); // Basari log'u

        } catch (Exception e) { // Hata yakalama
            log.error("CSV satirlari channel'a gonderilirken hata: {}", e.getMessage()); // Hata log'u
            throw new RuntimeException("CSV satirlari channel'a gonderilemedi: " + e.getMessage(), e); // Hata firlat
        }
    }

    public static class CsvProcessingResult { // CSV işleme sonucu sınıfı
        private boolean success; // Başarı durumu
        private String message; // Mesaj
        private String errorMessage; // Hata mesajı
        private int studentCount; // Öğrenci sayısı

        public boolean isSuccess() {
            return success;
        } // Başarı durumu getter

        public void setSuccess(boolean success) {
            this.success = success;
        } // Başarı durumu setter

        public String getMessage() {
            return message;
        } // Mesaj getter

        public void setMessage(String message) {
            this.message = message;
        } // Mesaj setter

        public String getErrorMessage() {
            return errorMessage;
        } // Hata mesajı getter

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        } // Hata mesajı setter

        public int getStudentCount() {
            return studentCount;
        } // Öğrenci sayısı getter

        public void setStudentCount(int studentCount) {
            this.studentCount = studentCount;
        } // Öğrenci sayısı setter
    }
} 