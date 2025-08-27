package com.example.backend.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class IpAddressResponse {
    private Long id;
    private String ipAddress;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isAssigned; // Öğrenciye atanmış olup olmadığını belirtir
    private Integer assignedCount; // Kaç öğrenciye atanmış olduğunu belirtir
}
