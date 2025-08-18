package com.example.backend.request;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class CreateIpAddressRequest {

    @NotBlank(message = "IP adresi boş olamaz")
    @Size(max = 50, message = "IP adresi 50 karakterden uzun olamaz")
    private String ipInput;

    @Size(max = 500, message = "Açıklama 500 karakterden uzun olamaz")
    private String description;
}
