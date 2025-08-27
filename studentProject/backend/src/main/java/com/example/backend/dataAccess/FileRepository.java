package com.example.backend.dataAccess;

import com.example.backend.entities.File; // Dosya entity'si
import org.springframework.data.jpa.repository.JpaRepository; // JPA repository arayüzü
import org.springframework.data.jpa.repository.Query; // Query anotasyonu
import org.springframework.stereotype.Repository; // Repository anotasyonu

import java.time.LocalDateTime; // Yerel tarih zaman
import java.util.List; // Liste

@Repository
public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findByProcessedAtBetweenOrderByProcessedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    List<File> findByStatusOrderByProcessedAtDesc(File.FileStatus status);

    List<File> findByFileNameContainingIgnoreCaseOrderByProcessedAtDesc(String fileName);

    @Query("SELECT f FROM File f ORDER BY f.processedAt DESC")
    List<File> findRecentFiles(org.springframework.data.domain.Pageable pageable);

    long countByStatus(File.FileStatus status);

    List<File> findAllByOrderByProcessedAtDesc();

    @Query("SELECT COALESCE(SUM(f.studentCount), 0) FROM File f WHERE f.status = 'DONE'")
    long getTotalProcessedStudents();
} 