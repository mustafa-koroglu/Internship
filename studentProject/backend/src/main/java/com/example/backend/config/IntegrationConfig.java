package com.example.backend.config;

import com.example.backend.entities.Student;
import com.example.backend.service.concretes.StudentManager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.messaging.MessageChannel;

@Configuration
@EnableIntegration
@RequiredArgsConstructor
@Slf4j
public class IntegrationConfig {

    private final StudentManager studentManager;

    @Bean
    public MessageChannel csvLineChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageChannel studentChannel() {
        return new DirectChannel();
    }

    @ServiceActivator(inputChannel = "studentChannel")
    public void saveStudent(Student student) {
        try {
            log.info("Ogrenci kaydediliyor: {} {} ({})",
                    student.getName(), student.getSurname(), student.getNumber());

            // Ogrenciyi veritabanina kaydet
            Student savedStudent = studentManager.save(student);

            log.info("Ogrenci basariyla kaydedildi. ID: {}", savedStudent.getId());

        } catch (Exception e) {
            log.error("Ogrenci kaydedilirken hata olustu: {} {} - Hata: {}",
                    student.getName(), student.getSurname(), e.getMessage());
            throw new RuntimeException("Ogrenci kaydedilemedi: " + e.getMessage(), e);
        }
    }
} 