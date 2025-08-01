package com.example.backend.entities;

import jakarta.persistence.*; // JPA anotasyonları
import lombok.AllArgsConstructor; // Tüm alanlar için constructor
import lombok.Data; // Getter, setter, toString, equals, hashCode
import lombok.NoArgsConstructor; // Parametresiz constructor
import org.hibernate.annotations.CreationTimestamp; // Oluşturma zaman damgası

import java.time.LocalDateTime; // Yerel tarih zaman

@Entity
@Table(name = "files")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class File {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "full_file_name", nullable = false)
    private String fullFileName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FileStatus status;

    @CreationTimestamp // Oluşturma zaman damgası
    @Column(name = "processed_at", nullable = false) // İşlenme tarihi sütunu
    private LocalDateTime processedAt; // Dosya işlenme tarihi

    @Column(name = "student_count")
    private Integer studentCount;

    @Column(name = "description", length = 1000)
    private String description;

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