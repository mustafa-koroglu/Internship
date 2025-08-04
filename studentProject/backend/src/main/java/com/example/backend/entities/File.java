package com.example.backend.entities; // Entity paketi

import jakarta.persistence.*; // JPA anotasyonları
import lombok.AllArgsConstructor; // Tüm alanlar için constructor
import lombok.Data; // Getter, setter, toString, equals, hashCode
import lombok.NoArgsConstructor; // Parametresiz constructor
import org.hibernate.annotations.CreationTimestamp; // Oluşturma zaman damgası

import java.time.LocalDateTime; // Yerel tarih zaman

@Entity // JPA entity anotasyonu
@Table(name = "files") // Tablo adı
@Data // Lombok data anotasyonu
@NoArgsConstructor // Parametresiz constructor
@AllArgsConstructor // Tüm alanlar için constructor
public class File { // Dosya entity sınıfı

    @Id // Primary key anotasyonu
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Otomatik artan ID
    private Long id; // Dosya ID'si

    @Column(name = "file_name", nullable = false) // Dosya adı sütunu
    private String fileName; // Dosya adı

    @Column(name = "full_file_name", nullable = false) // Tam dosya adı sütunu
    private String fullFileName; // Tam dosya adı

    @Enumerated(EnumType.STRING) // Enum string olarak sakla
    @Column(name = "status", nullable = false) // Durum sütunu
    private FileStatus status; // Dosya durumu

    @CreationTimestamp // Oluşturma zaman damgası
    @Column(name = "processed_at", nullable = false) // İşlenme tarihi sütunu
    private LocalDateTime processedAt; // Dosya işlenme tarihi

    @Column(name = "student_count") // Öğrenci sayısı sütunu
    private Integer studentCount; // İşlenen öğrenci sayısı

    @Column(name = "description", length = 1000) // Açıklama sütunu
    private String description; // Dosya açıklaması

    public enum FileStatus { // Dosya durumu enum'u
        DONE, // Başarıyla işlendi
        FAIL // Hata ile işlendi
    }

    public File(String fileName, String fullFileName, int studentCount, String description) { // DONE durumu için kurucu
        this.fileName = fileName; // Dosya adını ayarla
        this.fullFileName = fullFileName; // Tam dosya adını ayarla
        this.status = FileStatus.DONE; // Durumu DONE yap
        this.studentCount = studentCount; // Öğrenci sayısını ayarla
        this.description = description; // Açıklamayı ayarla
    }

    public File(String fileName, String fullFileName, String errorDescription) { // FAIL durumu için kurucu
        this.fileName = fileName; // Dosya adını ayarla
        this.fullFileName = fullFileName; // Tam dosya adını ayarla
        this.status = FileStatus.FAIL; // Durumu FAIL yap
        this.studentCount = 0; // Öğrenci sayısını 0 yap
        this.description = errorDescription; // Hata açıklamasını ayarla
    }
} 