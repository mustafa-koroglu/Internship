package com.example.backend.utility; // Utility paketi

import com.example.backend.entities.Student; // Öğrenci entity'si
import com.example.backend.response.StudentResponse; // Öğrenci yanıt DTO'su

public class ResponseMapper { // Yanıt dönüştürücü sınıfı

    public static StudentResponse toStudentResponse(Student student) { // Öğrenci entity'sini DTO'ya dönüştürme metodu
        if (student == null) { // Öğrenci null ise
            return null; // Null döndür
        }
        
        StudentResponse response = new StudentResponse(); // Yeni yanıt nesnesi oluştur
        response.setId(student.getId()); // ID'yi ayarla
        response.setName(student.getName()); // İsmi ayarla
        response.setSurname(student.getSurname()); // Soyismi ayarla
        response.setNumber(student.getNumber()); // Numarayı ayarla
        response.setVerified(student.getVerified()); // Onay durumunu ayarla
        response.setView(student.getView()); // Görünürlük durumunu ayarla
        return response; // Yanıtı döndür
    }
} 