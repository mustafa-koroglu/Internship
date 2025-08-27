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

    @CreationTimestamp
    @Column(name = "processed_at", nullable = false)
    private LocalDateTime processedAt;

    @Column(name = "student_count")
    private Integer studentCount;

    @Column(name = "description", length = 1000)
    private String description;

    public enum FileStatus {
        DONE,
        FAIL
    }

    public File(String fileName, String fullFileName, int studentCount, String description) {
        this.fileName = fileName;
        this.fullFileName = fullFileName;
        this.status = FileStatus.DONE;
        this.studentCount = studentCount;
        this.description = description;
    }

    public File(String fileName, String fullFileName, String errorDescription) {
        this.fileName = fileName;
        this.fullFileName = fullFileName;
        this.status = FileStatus.FAIL;
        this.studentCount = 0;
        this.description = errorDescription;
    }
} 