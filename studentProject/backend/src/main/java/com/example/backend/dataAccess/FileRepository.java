package com.example.backend.dataAccess;

import com.example.backend.entities.File; // Dosya entity'si
import org.springframework.data.jpa.repository.JpaRepository; // JPA repository arayüzü
import org.springframework.data.jpa.repository.Query; // Query anotasyonu
import org.springframework.stereotype.Repository; // Repository anotasyonu

import java.time.LocalDateTime; // Yerel tarih zaman
import java.util.List; // Liste

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByProcessedAtBetweenOrderByProcessedAtDesc(LocalDateTime startDate, LocalDateTime endDate); // Tarih aralığında dosya arama metodu

    List<File> findByStatusOrderByProcessedAtDesc(File.FileStatus status); // Duruma göre dosya arama metodu

    List<File> findByFileNameContainingIgnoreCaseOrderByProcessedAtDesc(String fileName); // Dosya adına göre arama metodu

    @Query("SELECT f FROM File f ORDER BY f.processedAt DESC") // En son işlenen dosyaları getir sorgusu
    List<File> findRecentFiles(org.springframework.data.domain.Pageable pageable); // Son dosyaları getir metodu

    long countByStatus(File.FileStatus status); // Duruma göre dosya sayısı metodu

    List<File> findAllByOrderByProcessedAtDesc(); // Tüm dosyaları tarihe göre sırala metodu

    @Query("SELECT COALESCE(SUM(f.studentCount), 0) FROM File f WHERE f.status = 'DONE'") // Toplam işlenen öğrenci sayısı sorgusu
    long getTotalProcessedStudents(); // Toplam işlenen öğrenci sayısı metodu
} 