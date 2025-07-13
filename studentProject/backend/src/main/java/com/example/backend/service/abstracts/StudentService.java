package com.example.backend.service.abstracts;

import com.example.backend.entities.Student;

import java.util.List;

// Öğrenci ile ilgili servis işlemlerini tanımlayan arayüz
public interface StudentService {
    // Tüm öğrencileri getirir
    List<Student> findAll();
    // ID'ye göre öğrenci getirir
    Student findById(int id);
    // Yeni öğrenci kaydeder
    Student save(Student student);
    // Öğrenci günceller
    Student update(int id,Student student);
    // ID'ye göre öğrenci siler
    void deleteById(int id);
    // İsme göre arama yapar
    List<Student> searchByName(String name);
    // İsim veya soyisme göre arama yapar
    List<Student> searchByNameOrSurname(String searchTerm);
    // Numaraya göre arama yapar
    List<Student> searchByNumber(String number);
}

