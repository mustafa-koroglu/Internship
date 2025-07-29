package com.example.backend.service.concretes;

import com.example.backend.entities.Student;
import com.example.backend.dataAccess.StudentsRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV dosya işleme servisi.
 * 
 * Bu servis, belirtilen klasördeki CSV dosyalarını otomatik olarak okur
 * ve içindeki öğrenci verilerini veritabanına kaydeder.
 * 
 * Öğrenci onay sistemi entegrasyonu:
 * - CSV'den okunan öğrenciler otomatik olarak onaylanmamış olarak işaretlenir
 * - verified = false: Admin onayı bekler
 * - view = false: Kullanıcılara gösterilmez
 * - Admin onayladıktan sonra kullanıcılara görünür hale gelir
 * 
 * İş kuralları:
 * - CSV dosyası header satırı içermelidir (name, surname, number)
 * - Dosya işlendikten sonra .done.csv uzantısı alır
 * - Duplicate öğrenci numaraları kontrol edilir
 * - Hatalı satırlar loglanır ve atlanır
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CsvProcessingService {

    private final StudentsRepository studentsRepository;

    @Value("${csv.watch.directory:./csv-files}")
    private String csvWatchDirectory;

    /**
     * CSV dosyalarını işler.
     * 
     * Bu metod, belirtilen klasördeki tüm .csv dosyalarını bulur ve işler.
     * .done.csv uzantılı dosyalar işlenmez (zaten işlenmiş dosyalar).
     * 
     * İşlem adımları:
     * 1. Klasör varlığını kontrol eder, yoksa oluşturur
     * 2. .csv dosyalarını bulur (.done.csv hariç)
     * 3. Her dosyayı ayrı ayrı işler
     * 4. İşlenen dosyaları .done.csv olarak yeniden adlandırır
     */
    public void processCsvFiles() {
        log.info("CSV dosyaları işleniyor...");
        
        try {
            // CSV dosyalarının bulunduğu klasörü kontrol et
            Path directory = Paths.get(csvWatchDirectory);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
                log.info("CSV klasörü oluşturuldu: {}", csvWatchDirectory);
                return;
            }

            // .csv ile biten dosyaları bul (.done.csv ve .fail.csv hariç)
            File[] csvFiles = directory.toFile().listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".csv") && 
                !name.toLowerCase().endsWith(".done.csv") && 
                !name.toLowerCase().endsWith(".fail.csv"));

            if (csvFiles == null || csvFiles.length == 0) {
                log.info("İşlenecek CSV dosyası bulunamadı.");
                return;
            }

            // Her CSV dosyasını ayrı ayrı işle
            for (File csvFile : csvFiles) {
                processCsvFile(csvFile);
            }

        } catch (Exception e) {
            log.error("CSV dosyaları işlenirken hata oluştu: {}", e.getMessage(), e);
        }
    }

    /**
     * Tek bir CSV dosyasını işler.
     * 
     * Bu metod, belirtilen CSV dosyasını okur ve içindeki öğrenci verilerini
     * veritabanına kaydeder. CSV'den okunan öğrenciler onaylanmamış olarak işaretlenir.
     * 
     * İşlem adımları:
     * 1. CSV dosyasını açar ve header satırını okur
     * 2. Her veri satırını parse eder
     * 3. Öğrenci verilerini veritabanına kaydeder
     * 4. Dosyayı .done.csv olarak yeniden adlandırır
     * 
     * Öğrenci onay sistemi:
     * - CSV'den okunan öğrenciler: verified=false, view=false
     * - Admin onayı bekler
     * - Kullanıcılara gösterilmez
     * 
     * @param csvFile İşlenecek CSV dosyası
     */
    private void processCsvFile(File csvFile) {
        log.info("CSV dosyası işleniyor: {}", csvFile.getName());
        
        CSVReader reader = null;
        boolean processingSuccess = true; // İşleme başarı durumunu takip et
        
        try {
            reader = new CSVReader(new FileReader(csvFile));
            
            // Header satırını oku (name, surname, number)
            String[] header = reader.readNext();
            if (header == null) {
                log.warn("CSV dosyası boş: {}", csvFile.getName());
                renameFileToFail(csvFile, "Dosya boş");
                return;
            }

            // Header'ın geçerli olup olmadığını kontrol et
            if (!isValidHeader(header)) {
                log.error("Geçersiz header formatı: {} - Dosya: {}", String.join(",", header), csvFile.getName());
                renameFileToFail(csvFile, "Geçersiz header formatı - Beklenen: name,surname,number");
                processingSuccess = false; // Header geçersizse başarısız olarak işaretle
                return;
            }

            List<Student> students = new ArrayList<>();
            String[] line;
            int lineNumber = 1; // Header'dan sonra 1'den başlar

            // Her veri satırını oku ve parse et
            while ((line = reader.readNext()) != null) {
                lineNumber++;
                try {
                    Student student = parseStudentFromCsvLine(line, header);
                    if (student != null) {
                        students.add(student);
                    }
                } catch (Exception e) {
                    log.error("Satır {} işlenirken hata: {} - Dosya: {}", lineNumber, e.getMessage(), csvFile.getName());
                    processingSuccess = false; // Satır işleme hatası durumunda başarısız olarak işaretle
                }
            }

            // Öğrencileri veritabanına kaydet (duplicate kontrolü ile)
            if (!students.isEmpty()) {
                int savedCount = 0;
                int duplicateCount = 0;
                for (Student student : students) {
                    try {
                        // Öğrenci numarası zaten var mı kontrol et
                        List<Student> existingStudents = studentsRepository.findByNumber(student.getNumber());
                        if (existingStudents.isEmpty()) {
                            // Yeni öğrenci - CSV'den okunan öğrenciler onaylanmamış ve görünmez olarak işaretlenir
                            student.setVerified(false);  // Admin onayı bekler
                            student.setView(false);      // Kullanıcılara gösterilmez
                            
                            studentsRepository.save(student);
                            savedCount++;
                            log.info("Yeni öğrenci kaydedildi: {} {} ({})", student.getName(), student.getSurname(), student.getNumber());
                        } else {
                            // Duplicate öğrenci - mevcut öğrenciyi güncelle (eğer onaylanmamışsa)
                            Student existingStudent = existingStudents.get(0);
                            if (!existingStudent.getVerified()) {
                                // Onaylanmamış öğrenciyi güncelle
                                existingStudent.setName(student.getName());
                                existingStudent.setSurname(student.getSurname());
                                studentsRepository.save(existingStudent);
                                savedCount++;
                                log.info("Mevcut onaysız öğrenci güncellendi: {} {} ({})", student.getName(), student.getSurname(), student.getNumber());
                            } else {
                                // Zaten onaylanmış öğrenci - atla
                                duplicateCount++;
                                log.warn("Zaten onaylanmış öğrenci atlandı: {} {} ({})", student.getName(), student.getSurname(), student.getNumber());
                            }
                        }
                    } catch (Exception e) {
                        log.error("Öğrenci kaydedilirken hata: {} - Numara: {}", e.getMessage(), student.getNumber());
                        processingSuccess = false; // Veritabanı hatası durumunda başarısız olarak işaretle
                    }
                }
                log.info("{} öğrenci kaydı başarıyla işlendi, {} duplicate atlandı. Dosya: {}", savedCount, duplicateCount, csvFile.getName());
            } else {
                // Hiç öğrenci okunamadıysa hata olarak işaretle
                log.warn("CSV dosyasından hiç geçerli öğrenci okunamadı: {}", csvFile.getName());
                processingSuccess = false;
            }

        } catch (IOException | CsvValidationException e) {
            log.error("CSV dosyası okunurken hata: {} - Dosya: {}", e.getMessage(), csvFile.getName(), e);
            processingSuccess = false; // Dosya okuma hatası durumunda başarısız olarak işaretle
        } finally {
            // CSV reader'ı kapat
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("CSV reader kapatılırken hata: {}", e.getMessage());
                }
            }
            
            // İşleme sonucuna göre dosya ismini güncelle
            if (processingSuccess) {
                renameFileToDone(csvFile);
            } else {
                renameFileToFail(csvFile, "İşleme sırasında hata oluştu");
            }
        }
    }

    /**
     * Header'ın geçerli olup olmadığını kontrol eder.
     * 
     * Bu metod, CSV dosyasının header satırının beklenen formatta
     * olup olmadığını kontrol eder. Geçersiz header durumunda
     * dosya .fail uzantısı alır.
     * 
     * Desteklenen header formatları:
     * - name, surname, number (İngilizce)
     * - ad, soyad, numara (Türkçe)
     * - isim, soyad, ogrenci_no (Alternatif Türkçe)
     * 
     * @param header CSV başlık satırı
     * @return true eğer header geçerliyse, false değilse
     */
    private boolean isValidHeader(String[] header) {
        if (header == null || header.length < 3) {
            log.warn("Header null veya 3 sütundan az: {}", header != null ? String.join(",", header) : "null");
            return false;
        }
        
        boolean hasName = false;
        boolean hasSurname = false;
        boolean hasNumber = false;
        
        log.info("Header kontrol ediliyor: {}", String.join(",", header));
        
        for (String column : header) {
            String columnLower = column.toLowerCase().trim();
            log.info("Sütun kontrol ediliyor: '{}' -> '{}'", column, columnLower);
            
            if (columnLower.equals("name") || columnLower.equals("ad") || columnLower.equals("isim")) {
                hasName = true;
                log.info("Name sütunu bulundu: {}", column);
            } else if (columnLower.equals("surname") || columnLower.equals("soyad") || columnLower.equals("lastname")) {
                hasSurname = true;
                log.info("Surname sütunu bulundu: {}", column);
            } else if (columnLower.equals("number") || columnLower.equals("numara") || columnLower.equals("ogrenci") || columnLower.equals("student") || columnLower.equals("ogrenci_no")) {
                hasNumber = true;
                log.info("Number sütunu bulundu: {}", column);
            }
        }
        
        boolean isValid = hasName && hasSurname && hasNumber;
        log.info("Header geçerliliği: hasName={}, hasSurname={}, hasNumber={}, isValid={}", hasName, hasSurname, hasNumber, isValid);
        
        return isValid;
    }

    /**
     * CSV satırından öğrenci nesnesi oluşturur.
     * 
     * Bu metod, CSV satırındaki verileri header'a göre eşleştirir
     * ve Student nesnesi oluşturur. Eksik veya hatalı veriler için
     * null döner ve loglanır.
     * 
     * Desteklenen header formatları:
     * - name, surname, number (İngilizce)
     * - ad, soyad, numara (Türkçe)
     * - isim, soyad, ogrenci_no (Alternatif Türkçe)
     * 
     * İş kuralları:
     * - En az 3 sütun gerekli (name, surname, number)
     * - Tüm alanlar boş olmamalı
     * - Header büyük/küçük harf duyarsız
     * 
     * @param line CSV satırı (veri)
     * @param header CSV başlık satırı
     * @return Student nesnesi veya null (hata durumunda)
     */
    private Student parseStudentFromCsvLine(String[] line, String[] header) {
        if (line.length < 3) {
            log.warn("Geçersiz satır formatı. En az 3 sütun gerekli.");
            return null;
        }

        try {
            Student student = new Student();
            
            // CSV sütunlarını header'a göre eşleştir
            for (int i = 0; i < header.length && i < line.length; i++) {
                String columnName = header[i].toLowerCase().trim();
                String value = line[i].trim();
                
                switch (columnName) {
                    case "name":
                    case "ad":
                    case "isim":
                        student.setName(value);
                        break;
                    case "surname":
                    case "soyad":
                    case "lastname":
                        student.setSurname(value);
                        break;
                    case "number":
                    case "numara":
                    case "student_number":
                    case "ogrenci_no":
                        student.setNumber(value);
                        break;
                }
            }

            // Gerekli alanların kontrolü
            if (student.getName() == null || student.getName().isEmpty() ||
                student.getSurname() == null || student.getSurname().isEmpty() ||
                student.getNumber() == null || student.getNumber().isEmpty()) {
                log.warn("Eksik öğrenci bilgileri. Satır atlandı.");
                return null;
            }

            return student;

        } catch (Exception e) {
            log.error("Öğrenci verisi parse edilirken hata: {}", e.getMessage());
            throw e; // Hatayı yukarı fırlat ki dosya .fail olsun
        }
    }

    /**
     * İşlenen CSV dosyasını .done.csv olarak yeniden adlandırır.
     * 
     * Bu metod, işlenen CSV dosyasını .done.csv uzantısı ile yeniden adlandırır.
     * Bu sayede dosya tekrar işlenmez ve hangi dosyaların işlendiği takip edilir.
     * 
     * Örnek: test.csv -> test.done.csv
     * 
     * @param csvFile Yeniden adlandırılacak CSV dosyası
     */
    private void renameFileToDone(File csvFile) {
        try {
            String newName = csvFile.getName().replace(".csv", ".done.csv");
            File doneFile = new File(csvFile.getParent(), newName);
            
            if (csvFile.renameTo(doneFile)) {
                log.info("Dosya işlendi olarak işaretlendi: {}", doneFile.getName());
            } else {
                log.warn("Dosya yeniden adlandırılamadı: {}", csvFile.getName());
            }
        } catch (Exception e) {
            log.error("Dosya yeniden adlandırılırken hata: {} - Dosya: {}", e.getMessage(), csvFile.getName());
        }
    }

    /**
     * Hata alan CSV dosyasını .fail.csv olarak yeniden adlandırır.
     * 
     * Bu metod, işleme sırasında hata alan CSV dosyasını .fail.csv uzantısı ile yeniden adlandırır.
     * Bu sayede hatalı dosyalar tekrar işlenmez ve hangi dosyaların hata aldığı takip edilir.
     * 
     * Örnek: test.csv -> test.fail.csv
     * 
     * @param csvFile Yeniden adlandırılacak CSV dosyası
     * @param errorMessage Hata mesajı (loglama için)
     */
    private void renameFileToFail(File csvFile, String errorMessage) {
        try {
            String newName = csvFile.getName().replace(".csv", ".fail.csv");
            File failFile = new File(csvFile.getParent(), newName);
            
            if (csvFile.renameTo(failFile)) {
                log.warn("Dosya hata aldı olarak işaretlendi: {} - Hata: {}", failFile.getName(), errorMessage);
            } else {
                log.error("Hatalı dosya yeniden adlandırılamadı: {} - Hata: {}", csvFile.getName(), errorMessage);
            }
        } catch (Exception e) {
            log.error("Hatalı dosya yeniden adlandırılırken hata: {} - Dosya: {} - Orijinal Hata: {}", 
                     e.getMessage(), csvFile.getName(), errorMessage);
        }
    }
} 