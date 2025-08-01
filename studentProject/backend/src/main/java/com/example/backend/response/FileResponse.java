package com.example.backend.response; // Response paketi

import com.example.backend.entities.File; // Dosya entity'si
import lombok.AllArgsConstructor; // Tüm alanlar için constructor
import lombok.Data; // Getter, setter, toString, equals, hashCode
import lombok.NoArgsConstructor; // Parametresiz constructor

import java.time.LocalDateTime; // Yerel tarih zaman

@Data // Lombok data anotasyonu
@NoArgsConstructor // Parametresiz constructor
@AllArgsConstructor // Tüm alanlar için constructor
public class FileResponse { // Dosya yanıtı DTO sınıfı

    private Long id; // Dosya ID'si

    private String fileName; // Dosya adı

    private String fullFileName; // Tam dosya adı

    private String status; // Dosya durumu

    private LocalDateTime processedAt; // İşlenme tarihi

    private Integer studentCount; // Öğrenci sayısı

    private String description; // Açıklama

    public static FileResponse fromEntity(File file) { // Entity'den DTO oluşturma metodu
        FileResponse response = new FileResponse(); // Yeni yanıt nesnesi oluştur
        response.setId(file.getId()); // ID'yi ayarla
        response.setFileName(file.getFileName()); // Dosya adını ayarla
        response.setFullFileName(file.getFullFileName()); // Tam dosya adını ayarla
        response.setStatus(file.getStatus().name()); // Durumu ayarla
        response.setProcessedAt(file.getProcessedAt()); // İşlenme tarihini ayarla
        response.setStudentCount(file.getStudentCount()); // Öğrenci sayısını ayarla
        response.setDescription(file.getDescription()); // Açıklamayı ayarla
        return response; // Yanıtı döndür
    }
} 