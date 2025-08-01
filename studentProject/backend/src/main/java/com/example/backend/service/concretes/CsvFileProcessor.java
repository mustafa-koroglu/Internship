package com.example.backend.service.concretes; // Service paketi

import com.example.backend.entities.Student; // Öğrenci entity'si
import com.example.backend.dataAccess.StudentsRepository; // Öğrenci repository'si
import com.opencsv.CSVReader; // CSV okuyucu
import com.opencsv.exceptions.CsvValidationException; // CSV doğrulama hatası
import lombok.RequiredArgsConstructor; // Constructor injection
import lombok.extern.slf4j.Slf4j; // Logging
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

    private final StudentsRepository studentsRepository; // Öğrenci repository'si

    public CsvProcessingResult processCsvFile(File csvFile) { // CSV dosyasını işleme metodu
        log.info("CSV dosyasi isleniyor: {}", csvFile.getName()); // Log mesajı
        
        CsvProcessingResult result = new CsvProcessingResult(); // Sonuç nesnesi oluştur
        CSVReader reader = null; // CSV okuyucu değişkeni
        
        try { // Hata yakalama bloğu
            reader = new CSVReader(new FileReader(csvFile)); // CSV okuyucu oluştur
            
            String[] header = reader.readNext(); // Header'ı oku
            if (header == null) { // Header yoksa
                result.setSuccess(false); // Başarısız olarak işaretle
                result.setErrorMessage("Dosya bos"); // Hata mesajı
                return result; // Sonucu döndür
            }

            if (!isValidHeader(header)) { // Header geçersizse
                result.setSuccess(false); // Başarısız olarak işaretle
                result.setErrorMessage("Gecersiz header formati - Beklenen: name,surname,number"); // Hata mesajı
                return result; // Sonucu döndür
            }

            List<Student> students = new ArrayList<>(); // Öğrenci listesi oluştur
            String[] line; // Satır değişkeni
            int lineNumber = 1; // Satır numarası

            while ((line = reader.readNext()) != null) { // Her satır için
                lineNumber++; // Satır numarasını artır
                try { // Hata yakalama bloğu
                    Student student = parseStudentFromCsvLine(line, header); // Satırdan öğrenci oluştur
                    if (student != null) { // Öğrenci geçerliyse
                        students.add(student); // Listeye ekle
                    }
                } catch (Exception e) { // Hata yakalama
                    log.error("Satir {} islenirken hata: {}", lineNumber, e.getMessage()); // Hata log'u
                    result.setSuccess(false); // Başarısız olarak işaretle
                    result.setErrorMessage("Satir " + lineNumber + " islenirken hata: " + e.getMessage()); // Hata mesajı
                    return result; // Sonucu döndür
                }
            }

            if (!students.isEmpty()) { // Öğrenci listesi boş değilse
                int savedCount = saveStudentsToDatabase(students); // Veritabanına kaydet
                result.setSuccess(true); // Başarılı olarak işaretle
                result.setStudentCount(savedCount); // Kaydedilen sayıyı ayarla
                result.setMessage(savedCount + " ogrenci basariyla islendi"); // Başarı mesajı
            } else { // Öğrenci listesi boşsa
                result.setSuccess(false); // Başarısız olarak işaretle
                result.setErrorMessage("CSV dosyasindan hic gecerli ogrenci okunamadi"); // Hata mesajı
            }

        } catch (IOException | CsvValidationException e) { // IO veya CSV hatası
            log.error("CSV dosyasi okunurken hata: {}", e.getMessage()); // Hata log'u
            result.setSuccess(false); // Başarısız olarak işaretle
            result.setErrorMessage("Dosya okuma hatasi: " + e.getMessage()); // Hata mesajı
        } finally { // Son işlem bloğu
            if (reader != null) { // Okuyucu varsa
                try { // Hata yakalama bloğu
                    reader.close(); // Okuyucuyu kapat
                } catch (IOException e) { // IO hatası
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

    private Student parseStudentFromCsvLine(String[] line, String[] header) { // CSV satırından öğrenci oluşturma metodu
        if (line.length < 3) { // Satır çok kısaysa
            log.debug("Satir cok kisa: {}", line.length); // Debug log'u
            return null; // Null döndür
        }

        Student student = new Student(); // Yeni öğrenci oluştur
        
        for (int i = 0; i < header.length && i < line.length; i++) { // Her sütun için
            String columnName = header[i].toLowerCase().trim(); // Sütun adını al
            String value = line[i].trim(); // Değeri al
            
            // Boş string kontrolü - boş string'leri null yap
            if (value == null || value.isEmpty() || value.equals("") || value.trim().isEmpty()) {
                value = null; // Boş değeri null yap
                log.debug("Bos deger bulundu: column={}, value='{}'", columnName, line[i]); // Debug log'u
            }
            
            switch (columnName) { // Sütun adına göre
                case "name": // İsim alanı
                    student.setName(value); // İsmi ayarla
                    break;
                case "surname": // Soyisim alanı
                    student.setSurname(value); // Soyismi ayarla
                    break;
                case "number": // Numara alanı
                    student.setNumber(value); // Numarayı ayarla
                    break;
            }
        }

        // Geçerlilik kontrolü
        boolean isValid = student.getName() != null && !student.getName().isEmpty() && 
                         student.getSurname() != null && !student.getSurname().isEmpty() && 
                         student.getNumber() != null && !student.getNumber().isEmpty();
        
        if (!isValid) {
            log.debug("Gecersiz ogrenci: name='{}', surname='{}', number='{}'", 
                     student.getName(), student.getSurname(), student.getNumber()); // Debug log'u
            return null; // Geçersiz öğrenci
        }

        return student; // Öğrenciyi döndür
    }

    private int saveStudentsToDatabase(List<Student> students) { // Öğrencileri veritabanına kaydetme metodu
        int savedCount = 0; // Kaydedilen sayı
        
        for (Student student : students) { // Her öğrenci için
            try { // Hata yakalama bloğu
                List<Student> existingStudents = studentsRepository.findByNumber(student.getNumber()); // Mevcut öğrencileri bul
                if (existingStudents.isEmpty()) { // Öğrenci yoksa
                    student.setVerified(false); // Onaylı değil
                    student.setView(false); // Görünür değil
                    studentsRepository.save(student); // Yeni öğrenciyi kaydet
                    savedCount++; // Sayacı artır
                } else { // Öğrenci varsa
                    Student existingStudent = existingStudents.get(0); // İlk öğrenciyi al
                    if (!existingStudent.getVerified()) { // Onaylı değilse
                        existingStudent.setName(student.getName()); // İsmi güncelle
                        existingStudent.setSurname(student.getSurname()); // Soyismi güncelle
                        studentsRepository.save(existingStudent); // Öğrenciyi kaydet
                        savedCount++; // Sayacı artır
                    }
                }
            } catch (Exception e) { // Hata yakalama
                log.error("Ogrenci kaydedilirken hata: {}", e.getMessage()); // Hata log'u
            }
        }
        
        return savedCount; // Kaydedilen sayıyı döndür
    }

    public static class CsvProcessingResult { // CSV işleme sonucu sınıfı
        private boolean success; // Başarı durumu
        private String message; // Mesaj
        private String errorMessage; // Hata mesajı
        private int studentCount; // Öğrenci sayısı

        public boolean isSuccess() { return success; } // Başarı durumu getter
        public void setSuccess(boolean success) { this.success = success; } // Başarı durumu setter
        
        public String getMessage() { return message; } // Mesaj getter
        public void setMessage(String message) { this.message = message; } // Mesaj setter
        
        public String getErrorMessage() { return errorMessage; } // Hata mesajı getter
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; } // Hata mesajı setter
        
        public int getStudentCount() { return studentCount; } // Öğrenci sayısı getter
        public void setStudentCount(int studentCount) { this.studentCount = studentCount; } // Öğrenci sayısı setter
    }
} 