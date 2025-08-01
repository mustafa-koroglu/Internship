package com.example.backend.service.abstracts; // Service abstract paketi

import com.example.backend.entities.Student; // Öğrenci entity'si
import java.util.List; // Liste

public interface StudentService { // Öğrenci servis arayüzü

    List<Student> findAll(); // Tüm öğrencileri getir metodu

    Student findById(int id); // ID ile öğrenci bul metodu

    Student save(Student student); // Öğrenci kaydet metodu

    Student update(int id, Student student); // Öğrenci güncelle metodu

    void deleteById(int id); // ID ile öğrenci sil metodu

    List<Student> search(String searchTerm); // Öğrenci ara metodu
    
    List<Student> findVerifiedAndViewable(); // Onaylanmış ve görünür öğrencileri getir metodu
    
    List<Student> findUnverified(); // Onaylanmamış öğrencileri getir metodu
    
    List<Student> findVerified(); // Onaylanmış öğrencileri getir metodu
    
    List<Student> searchVerifiedAndViewable(String searchTerm); // Onaylanmış öğrencilerde arama metodu
    
    Student approveStudent(int id); // Öğrenci onayla metodu
    
    Student setStudentVisibility(int id, boolean view); // Öğrenci görünürlük ayarla metodu
}