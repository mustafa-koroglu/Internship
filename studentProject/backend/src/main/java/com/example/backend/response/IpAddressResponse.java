package com.example.backend.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class IpAddressResponse {

    private Long id;
    private String ipAddress;
    private String description;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public IpAddressResponse(Long id, String ipAddress, String description, Boolean isActive,
                             LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.ipAddress = ipAddress;
        this.description = description;
        this.isActive = isActive;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
