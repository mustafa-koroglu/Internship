package com.example.backend.controller;

import com.example.backend.dataAccess.FileRepository;
import com.example.backend.entities.File;
import com.example.backend.response.FileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * File entity'si için REST API controller'ı.
 * İşlenen CSV dosyalarının bilgilerini yönetir.
 */
@RestController
@RequestMapping("/api/v3/files")
@RequiredArgsConstructor
@Slf4j
public class FileController {

    private final FileRepository fileRepository;

    /**
     * Tüm dosyaları getirir (en son işlenenler önce)
     * @return Dosya listesi
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FileResponse>> getAllFiles() {
        log.info("Tüm dosyalar getiriliyor");
        
        List<File> files = fileRepository.findAllByOrderByProcessedAtDesc();
        List<FileResponse> responses = files.stream()
                .map(FileResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Belirli bir ID'ye sahip dosyayı getirir
     * @param id Dosya ID'si
     * @return Dosya bilgisi
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FileResponse> getFileById(@PathVariable Long id) {
        log.info("Dosya getiriliyor, ID: {}", id);
        
        return fileRepository.findById(id)
                .map(file -> ResponseEntity.ok(FileResponse.fromEntity(file)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Belirli bir duruma sahip dosyaları getirir
     * @param status Dosya durumu (DONE veya FAIL)
     * @return Dosya listesi
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FileResponse>> getFilesByStatus(@PathVariable String status) {
        log.info("Duruma göre dosyalar getiriliyor: {}", status);
        
        try {
            File.FileStatus fileStatus = File.FileStatus.valueOf(status.toUpperCase());
            List<File> files = fileRepository.findByStatusOrderByProcessedAtDesc(fileStatus);
            List<FileResponse> responses = files.stream()
                    .map(FileResponse::fromEntity)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (IllegalArgumentException e) {
            log.error("Geçersiz dosya durumu: {}", status);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Dosya adına göre dosya arar
     * @param fileName Dosya adı (kısmi eşleşme)
     * @return Dosya listesi
     */
    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FileResponse>> searchFilesByName(@RequestParam String fileName) {
        log.info("Dosya adına göre arama yapılıyor: {}", fileName);
        
        List<File> files = fileRepository.findByFileNameContainingIgnoreCaseOrderByProcessedAtDesc(fileName);
        List<FileResponse> responses = files.stream()
                .map(FileResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * En son işlenen N dosyayı getirir
     * @param limit Limit sayısı (varsayılan: 10)
     * @return Dosya listesi
     */
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FileResponse>> getRecentFiles(@RequestParam(defaultValue = "10") int limit) {
        log.info("Son {} dosya getiriliyor", limit);
        
        List<File> files = fileRepository.findRecentFiles(PageRequest.of(0, limit));
        List<FileResponse> responses = files.stream()
                .map(FileResponse::fromEntity)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(responses);
    }

    /**
     * Belirli bir tarih aralığında işlenen dosyaları getirir
     * @param startDate Başlangıç tarihi (yyyy-MM-dd HH:mm:ss formatında)
     * @param endDate Bitiş tarihi (yyyy-MM-dd HH:mm:ss formatında)
     * @return Dosya listesi
     */
    @GetMapping("/date-range")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FileResponse>> getFilesByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {
        log.info("Tarih aralığına göre dosyalar getiriliyor: {} - {}", startDate, endDate);
        
        try {
            LocalDateTime start = LocalDateTime.parse(startDate);
            LocalDateTime end = LocalDateTime.parse(endDate);
            
            List<File> files = fileRepository.findByProcessedAtBetweenOrderByProcessedAtDesc(start, end);
            List<FileResponse> responses = files.stream()
                    .map(FileResponse::fromEntity)
                    .collect(Collectors.toList());
            
            return ResponseEntity.ok(responses);
        } catch (Exception e) {
            log.error("Tarih formatı hatası: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Dosya istatistiklerini getirir
     * @return İstatistik bilgileri
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FileStatsResponse> getFileStats() {
        log.info("Dosya istatistikleri getiriliyor");
        
        long totalFiles = fileRepository.count();
        long doneFiles = fileRepository.countByStatus(File.FileStatus.DONE);
        long failFiles = fileRepository.countByStatus(File.FileStatus.FAIL);
        long totalStudents = fileRepository.getTotalProcessedStudents();
        
        FileStatsResponse stats = new FileStatsResponse(totalFiles, doneFiles, failFiles, totalStudents);
        
        return ResponseEntity.ok(stats);
    }

    /**
     * Dosya istatistikleri için response sınıfı
     */
    public static class FileStatsResponse {
        private long totalFiles;
        private long doneFiles;
        private long failFiles;
        private long totalStudents;

        public FileStatsResponse(long totalFiles, long doneFiles, long failFiles, long totalStudents) {
            this.totalFiles = totalFiles;
            this.doneFiles = doneFiles;
            this.failFiles = failFiles;
            this.totalStudents = totalStudents;
        }

        // Getters
        public long getTotalFiles() { return totalFiles; }
        public long getDoneFiles() { return doneFiles; }
        public long getFailFiles() { return failFiles; }
        public long getTotalStudents() { return totalStudents; }
    }
} 